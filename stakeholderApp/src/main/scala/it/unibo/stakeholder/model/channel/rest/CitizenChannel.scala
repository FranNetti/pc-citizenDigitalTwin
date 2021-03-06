package it.unibo.stakeholder.model.channel.rest

import java.net.URI
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap, Executors}

import io.vertx.lang.scala.VertxExecutionContext
import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import io.vertx.scala.core.Vertx
import io.vertx.scala.core.http.{WebSocketBase, WebSocketConnectOptions}
import io.vertx.scala.ext.web.client.{WebClient, WebClientOptions}
import it.unibo.stakeholder.model.channel.TokenIdentifier
import it.unibo.stakeholder.model.channel.data.{Response, ServiceResponse}
import it.unibo.stakeholder.model.channel.digital_twin.CitizenDigitalTwin
import it.unibo.stakeholder.model.data.{Data, DataCategory}
import it.unibo.stakeholder.util.FutureService
import it.unibo.stakeholder.util.FutureService._
import it.unibo.stakeholder.model.channel.rest.vertx._
import it.unibo.stakeholder.model.channel.websocket.{CitizenProtocol, Failed, Ok, WebsocketRequest, WebsocketResponse, WebsocketUpdate}
import it.unibo.stakeholder.model.channel.coap._
import it.unibo.stakeholder.model.channel.parser.DataParserRegistry
import it.unibo.stakeholder.model.data.History.History
import monix.execution.atomic.AtomicInt
import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject
import org.eclipse.californium.core.{CoapClient, CoapObserveRelation, CoapResponse}

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
 * Citizen client implementation based on vertx webclient and coap client. It follow the [[CitizenDigitalTwin]] contract.
 * @param registry data parser
 * @param address the citizen service address
 * @param coapPort the port user by the citizen service for CoAP api protocol
 */
class CitizenChannel(override val citizenIdentifier : String,
                    registry : DataParserRegistry[JsonObject], address: URI,
                    coapPort : Int = 5683) extends CitizenDigitalTwin  with RestApiClient with RestClientServiceResponse {
  private val vertx = Vertx.vertx()
  private val coapFixedThreadpool = Executors.newFixedThreadPool(4)
  private implicit lazy val ctx = VertxExecutionContext(vertx.getOrCreateContext())
  private val httpClient = vertx.createHttpClient()
  private val atomicInt = AtomicInt(0)
  private val httpStateEndpoint = s"/citizens/$citizenIdentifier/state"
  private val coapStateEndpoint = s"coap://${address.getHost}:$coapPort/citizens/$citizenIdentifier/state"
  private val historyEndpoint = s"/citizens/$citizenIdentifier/history"

  override val webClient: WebClient = WebClient.create(vertx, WebClientOptions()
    .setDefaultHost(address.getHost)
    .setDefaultPort(address.getPort)
  )

  private def authorizationHeader(token : TokenIdentifier) : (String, String) = "Authorization" -> token.bearer

  private def enrichPathWithCategory(basePath : String, data : DataCategory) : String = basePath + s"?data_category=${data.name}"

  override def updateState(who: TokenIdentifier, data: Seq[Data]): FutureService[Seq[String]] = {
    val jsonPayload = Json.obj("data" -> fromSequenceToJsonArray(data))
    println("LOG : payload to sent = " + jsonPayload)
    val request = webClient.patch(httpStateEndpoint).putHeader(authorizationHeader(who)).sendJsonObjectFuture(jsonPayload)

    parseServiceResponseWhenComplete(request) {
      case (HttpCode.Ok, data) => Json.fromArrayString(data).getAsStringSeq.get
    }.toFutureService
  }
  override def readState(who: TokenIdentifier): FutureService[Seq[Data]] = {
    val request = webClient.get(httpStateEndpoint).putHeader(authorizationHeader(who)).sendFuture()
    parseServiceResponseWhenComplete(request) {
      case (HttpCode.Ok, data) => parseToSequence(data)
    }.toFutureService
  }
  override def readStateByCategory(who: TokenIdentifier, category: DataCategory): FutureService[Seq[Data]] = {
    val request = webClient.get(enrichPathWithCategory(httpStateEndpoint, category)).putHeader(authorizationHeader(who)).sendFuture()
    parseServiceResponseWhenComplete(request) {
      case (HttpCode.Ok, data) => parseToSequence(data)
    }.toFutureService
  }
  override def readHistory(who: TokenIdentifier, dataCategory: DataCategory, maxSize: Int): FutureService[History] = {
    val limitEndpoint = enrichPathWithCategory(historyEndpoint, dataCategory) + s"&limit=$maxSize"
    val request = webClient.get(limitEndpoint).putHeader(authorizationHeader(who)).sendFuture()
    def parseArrayToSequence(data : String) : Seq[Data] = {
      val obj = JsonConversion.arrayFromString(data).getOrElse(Json.emptyArr())
      obj.getAsObjectSeq match {
        case None => Seq.empty
        case Some(elements) => elements.map(registry.decode).collect { case Some(data) => data}
      }
    }
    parseServiceResponseWhenComplete(request) {
      case (HttpCode.Ok, data) => parseArrayToSequence(data)
    }.toFutureService
  }
  override def readHistoryData(who: TokenIdentifier, dataIdentifier: String): FutureService[Data] = {
    val request = webClient.get(historyEndpoint).putHeader(authorizationHeader(who)).sendFuture()
    parseServiceResponseWhenComplete(request) {
      case (HttpCode.Ok, data) => parseData(data).get
    }.toFutureService
  }

  override def createPhysicalLink(who: TokenIdentifier): FutureService[PhysicalLink] = {
    val (header, value) = authorizationHeader(who)
    val option = WebSocketConnectOptions()
      .setHost(address.getHost)
      .setPort(address.getPort)
      .setURI(httpStateEndpoint)
      .addHeader(header, value)
    httpClient.webSocketFuture(option).transform {
      case Success(webSocket) => Success(Response(new InnerPhysicalLink(webSocket)))
      case Failure(reason) => Failure[ServiceResponse[PhysicalLink]](reason)
    }.toFutureService
  }

  override def observeState(who: TokenIdentifier, dataCategory: DataCategory): FutureService[Observable[Data]] = {
    implicit val coapContext = scala.concurrent.ExecutionContext.fromExecutor(coapFixedThreadpool)
    val coapClient = new CoapClient(enrichPathWithCategory(coapStateEndpoint, dataCategory))
    val source = PublishSubject[Data]()
    Future[CoapObserveRelation] {
      coapClient.observeWithTokenAndWait(who.token, (result : CoapResponse) => {
        val _ = JsonConversion.objectFromString(result.getResponseText) match {
          case Some(element) => registry.decode(element) match {
            case Some(data) => source.onNext(data)
            case _ =>
          }
          case _ =>
        }
      })
    }(coapContext).transform {
      case Success(coapResponse) if coapResponse.isCanceled => Failure(new RuntimeException("observe failed"))
      case Success(_) => Success(Response(source))
    }(coapContext).toFutureService
  }

  private def parseToSequence(response : String) : Seq[Data] = {
    val obj = JsonConversion.objectFromString(response).getOrElse(Json.obj("data"->Json.emptyArr()))
    obj.getJsonArray("data").getAsObjectSeq match {
      case None => Seq.empty
      case Some(elements) => elements.map(registry.decode).collect { case Some(data) => data }
    }
  }

  private def parseData(response : String) : Option[Data] = {
    JsonConversion.objectFromString(response) match {
      case Some(elem) => registry.decode(elem)
      case _ => None
    }
  }

  private class InnerPhysicalLink(websocket : WebSocketBase) extends PhysicalLink {
    import CitizenProtocol._
    var promiseMap : ConcurrentMap[Int, Promise[Response[Seq[String]]]] = new ConcurrentHashMap()

    websocket.textMessageHandler(text => (updateParser.decode(text), responseParser.decode(text)) match {
      case (Some(WebsocketUpdate(json)), None) => registry.decode(json).foreach(subject.onNext)
      case (None, Some(WebsocketResponse(id, status))) =>
        val promise = promiseMap.get(id)
        status match {
          case Ok(seq) => promise.success(Response(seq))
          case Failed(reason) => promise.failure(new Exception(reason))
        }
      case _ =>
    })

    override def updateState(data: Seq[Data]): FutureService[Seq[String]] = {
      val requestId = atomicInt.incrementAndGet()
      val request = WebsocketRequest[JsonArray](requestId, data)
      val requestJson = CitizenProtocol.requestParser.encode(request)
      val promise = Promise[Response[Seq[String]]]()
      promiseMap.put(requestId, promise)
      websocket.writeTextMessage(requestJson)
      promise.future.toFutureService
    }
    private val subject = PublishSubject[Data]()
    override val updateDataStream: Observable[Data] = subject.executeAsync

    override def close(): Unit = {
      promiseMap.clear()
      websocket.close()
    }
  }

  private implicit def fromSequenceToJsonArray(seq: Seq[Data]): JsonArray = {
    val jsonToSend = seq.map(registry.encode).collect { case Some(data) => data }
    Json.arr(jsonToSend:_*)
  }

}
