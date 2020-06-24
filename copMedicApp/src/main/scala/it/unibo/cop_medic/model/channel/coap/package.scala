package it.unibo.cop_medic.model.channel

import java.util.UUID

import it.unibo.cop_medic.model.channel.rest.UserMiddleware
import org.eclipse.californium.core.{CoapClient, CoapHandler, CoapObserveRelation, CoapResponse}
import org.eclipse.californium.core.coap.{Request, Option => CoapOption}
import org.eclipse.californium.core.server.resources.CoapExchange

import scala.util.Random

package object coap {
  private val MAX_OTHER_OPTIONS = 4080
  val TOKEN_HEADER_CODE = 2048
  val FIRST_FREE_POSITION = 2049
  val rand = new Random()

  def generateCoapSecret() : (Int, String) = {
    val key = rand.nextInt(MAX_OTHER_OPTIONS) + FIRST_FREE_POSITION
    val string = UUID.randomUUID().toString
    (key, string)
  }
  implicit class RichExchangeCaop(ex : CoapExchange) {
    def getQueryParams(query : String) : Option[String] = Option(ex.getQueryParameter(query))

    import collection.JavaConverters._
    def getAuthToken : Option[String] = getOption(TOKEN_HEADER_CODE).flatMap(UserMiddleware.extractToken)

    def getOption(code : Int) : Option[String] = {
      ex.getRequestOptions.getOthers.asScala
        .filter(_.getNumber == code)
        .map(_.getStringValue)
        .headOption
    }
  }
  implicit def funToHandler(fun : CoapResponse => Unit) : CoapHandler = new CoapHandler {
    override def onLoad(response: CoapResponse): Unit = fun(response)

    override def onError(): Unit = {}
  }
  implicit class RichClient(ex : CoapClient) {
    def coapTokenOption(token : String) : CoapOption = new CoapOption(TOKEN_HEADER_CODE, UserMiddleware.asToken(token))

    def observeWithToken(token : String, coapHandler: CoapHandler) : CoapObserveRelation =
      ex.observe(Request.newGet().setObserve() >>> token, coapHandler)
    def observeWithTokenAndWait(token : String, coapHandler: CoapHandler) : CoapObserveRelation =
      ex.observeAndWait(Request.newGet().setObserve() >>> token, coapHandler)

    def getWithToken(token : String) : CoapResponse =
      ex advanced Request.newGet() >>> token

    def putWithOptions(payload : String, contentFormat: ContentFormat, optionSequence : (Int, String) *): CoapResponse = {
      val request = Request.newPut()
      request.setPayload(payload)
      val options = request.getOptions
      options.setContentFormat(contentFormat.code)
      optionSequence.foreach {
        case (key, value) => options.addOption(new CoapOption(key, value))
      }
      ex.advanced(request)
    }

    private implicit class RichRequest(request: Request) {
      def >>>(token: String): Request = {
        val options = request.getOptions
        options.addOption(coapTokenOption(token))
        request
      }
    }

  }
}
