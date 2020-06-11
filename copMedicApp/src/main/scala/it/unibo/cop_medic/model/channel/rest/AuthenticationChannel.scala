package it.unibo.cop_medic.model.channel.rest

import java.net.URI

import io.vertx.lang.scala.VertxExecutionContext
import io.vertx.lang.scala.json.{Json, JsonObject}
import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.web.client.{WebClient, WebClientOptions}
import it.unibo.cop_medic.model.channel.parser.AuthenticationParsers
import it.unibo.cop_medic.model.channel._
import it.unibo.cop_medic.util.{FutureService, SystemUser}
import it.unibo.cop_medic.util.FutureService._

import scala.concurrent.Future

object AuthenticationChannel {
  val LOGIN = s"/login"
  val VERIFY = s"/verify?token=%s"
  val LOGOUT = s"/logout"
  val REFRESH = s"/refreshToken"

  /**
   * Create an AuthenticationService proxy client using the same interface of the service.
   * @param serviceUri Uri where the real Authentication Service is located
   * @return AuthenticationService instance
   */
  def apply(serviceUri: URI): AuthenticationChannel = new AuthenticationChannel(serviceUri)

  /**
   * Create an AuthenticationService proxy client using the same interface of the service.
   * @param host The http host where the real Authentication Service is located
   * @param port The http port where the real Authentication Service is located
   * @return AuthenticationService instance
   */
  def apply(host: String, port: Int = 8080): AuthenticationChannel = new AuthenticationChannel(URI.create(s"http://$host:$port"))
}

/**
 * Authentication client implementation based on vertx webclient. It follow the [[AuthenticationService]] contract.
 * @param serviceUri  Uri where the real Authentication Service is located.
 */
class AuthenticationChannel(serviceUri: URI) extends AuthenticationService with RestApiClient with RestClientServiceResponse {

  import AuthenticationChannel._

  private val vertx = Vertx.vertx()
  private val clientOptions =  WebClientOptions()
    .setFollowRedirects(true)
    .setDefaultPort(serviceUri.getPort)

  override val webClient: WebClient = WebClient.create(vertx, clientOptions)

  private implicit val executionContext: VertxExecutionContext = VertxExecutionContext(vertx.getOrCreateContext())

  override def login(email: String, password: String): FutureService[AuthenticationInfo] = {
    val request = s"${serviceUri.toString}$LOGIN"
    val requestBody = Json.emptyObj().put("email", email).put("password", password)
    parseServiceResponseWhenComplete(webClient.post(request).sendJsonObjectFuture(requestBody)) {
      case (HttpCode.Created, authenticationInfo) => parseAuthenticationInfo(new JsonObject(authenticationInfo))
    }.toFutureService
  }

  override def verifyToken(identifier: TokenIdentifier): FutureService[SystemUser] = {
    val request = s"${serviceUri.toString}$VERIFY".format(identifier.token)
    parseServiceResponseWhenComplete(webClient.get(request).sendFuture()) {
      case (HttpCode.Ok, body) => parseUser(Json.fromObjectString(body))
    }.toFutureService
  }

  override def refresh(identifier: TokenIdentifier): FutureService[Token] = {
    val request = s"${serviceUri.toString}$REFRESH"
    parseServiceResponseWhenComplete(webClient.post(request).putHeader(getAuthorizationHeader(identifier)).sendFuture()) {
      case (HttpCode.Created, newToken) => parseToken(new JsonObject(newToken))
    }.toFutureService
  }

  override def logout(identifier: TokenIdentifier): FutureService[Boolean] = {
    val request = s"${serviceUri.toString}$LOGOUT"
    parseServiceResponseWhenComplete(webClient.get(request).putHeader(getAuthorizationHeader(identifier)).sendFuture()) {
      case (HttpCode.NoContent, "") => true
    }.toFutureService
  }

  def close(): Future[Unit] = {
    webClient.close()
    vertx.closeFuture()
  }

  private def getAuthorizationHeader(token: TokenIdentifier): (String, String) = "Authorization" -> s"Bearer ${token.token}"

  protected def parseAuthenticationInfo(jsonObject: JsonObject): AuthenticationInfo = {
    AuthenticationParsers.AuthInfoParser.decode(jsonObject).get
  }

  protected def parseToken(jsonObject: JsonObject): Token = {
    AuthenticationParsers.TokenParser.decode(jsonObject).get
  }

  protected def parseUser(jsonObject: JsonObject): SystemUser = {
    AuthenticationParsers.SystemUserParser.decode(jsonObject).get
  }
}
