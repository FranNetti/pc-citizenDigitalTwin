package it.unibo.cop_medic.model.channel.rest

import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import io.vertx.scala.core.http.{HttpServerResponse, ServerWebSocket}
import it.unibo.cop_medic.model.channel.rest.HttpCode._

object vertx {
  protected[vertx] def tryOrNone[E](some : => E) : Option[E] = try {
    Some(some)
  } catch {
    case e : Exception => None
  }

  protected[vertx] def getOrNone[E](string : String, jsonObject: JsonObject)(some : => E): Option[E] =
    if(jsonObject containsKey string) tryOrNone(some)
    else None

  /**
   * other conversion that wrap the result of Json.fromObjectString and Json.fromArrayString with Option.
   */
  object JsonConversion {
    /**
     * create a json object from a string, if it possibile
     * @param buffer The json string
     * @return Some(object) if the decoding fails None otherwise
     */
    def objectFromString(buffer : String) : Option[JsonObject] = tryOrNone(Json.fromObjectString(buffer))

    /**
     * create a json array from a string, if it possibile
     * @param buffer The json string
     * @return Some(array) if the decoding fails None otherwise
     */
    def arrayFromString(buffer : String) : Option[JsonArray] = tryOrNone(Json.fromArrayString(buffer))
  }

  implicit class RichJson(json : JsonObject) {
    def getAsString(s : String) : Option[String] = getOrNone(s, json){ json.getString(s) }
    def getAsObject(s : String) : Option[JsonObject] = getOrNone(s, json){ json.getJsonObject(s) }
    def getAsLong(s : String) : Option[Long] = getOrNone(s, json){ json.getLong(s) }
    def getAsInt(s : String) : Option[Int] = getOrNone(s, json){ json.getInteger(s) }
    def getAsDouble(s : String) : Option[Double] = getOrNone(s, json){ json.getDouble(s) }
    def getAsBoolean(s : String) : Option[Boolean] = getOrNone(s, json){ json.getBoolean(s)}
    def getAsArray(s : String) : Option[JsonArray] = getOrNone(s, json) { json.getJsonArray(s)}
  }

  implicit class RichJsonArray(json : JsonArray) {
    def getAsObjectSeq : Option[Seq[JsonObject]] = {
      val elems = json.size() - 1
      try {
        val objects = (0 to elems).map (json.getJsonObject)
        Some(objects)
      } catch {
        case _: Exception => None
      }
    }
    def getAsStringSeq : Option[Seq[String]] = {
      val elems = json.size() - 1
      try {
        val objects = (0 to elems).map(json.getString)
        Some(objects)
      } catch {
        case _: Exception => None
      }
    }
  }

  implicit class RichHttpServerResponse(response: HttpServerResponse) {

    def setResponse(response: (HttpCode, String)): Unit = setResponse(response._1.code, response._2)

    private def setResponse(statusCode: Int, obj: JsonObject): Unit = setResponse(statusCode, obj.encode())
    private def setResponse(statusCode: Int, body: String): Unit = {
      response.setStatusCode(statusCode).end(body)
    }

    def setInternalError(message: String = "Internal Error"): Unit = setResponse(InternalError, message)
    def setNotFound(message: String = "Not Found"): Unit = setResponse(BadRequest, message)
    def setForbidden(message: String = "Forbidden"): Unit = setResponse(Forbidden, message)
    def setNotAuthorized (message: String = "Not authorized"): Unit = setResponse(Unauthorized, message)
    def setBadRequest(message: String = "Bad request"): Unit = setResponse(BadRequest, message)
    def setConflict(message: String = "Conflict"): Unit = setResponse(Conflict, message)
    def setCreated(obj: JsonObject): Unit = setResponse(Created, obj)
    def setCreated(obj: String): Unit = setResponse(Created, obj)
    def setOk (obj: JsonObject): Unit = setResponse(Ok, obj)
    def setOk (obj: JsonArray): Unit = setResponse(Ok, obj.encode())
    def setNoContent(): Unit = setResponse(NoContent, "")
  }

  implicit class RichServerWebSocket(webSocket : ServerWebSocket) {
    def rejectInternalError(): Unit = webSocket.reject(InternalError)
    def rejectForbidden(): Unit = webSocket.reject(Forbidden)
    def rejectNotAuthorized(): Unit = webSocket.reject(Unauthorized)
    def rejectBadContent(): Unit = webSocket.reject(BadRequest)
    def writeTextJsonObject(json : JsonObject): ServerWebSocket = webSocket.writeTextMessage(json.encode())
    def writeTextJsonArray(json : JsonArray): ServerWebSocket = webSocket.writeTextMessage(json.encode())
  }

  private implicit def fromHttpCodeToInt(code: HttpCode): Int = code.code
}
