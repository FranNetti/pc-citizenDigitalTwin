package it.unibo.cop_medic.model.channel.rest

import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import io.vertx.scala.core.http.{HttpServerResponse, ServerWebSocket}

object vertx {
  protected[vertx] def tryOrNone[E](some : => E) : Option[E] = try {
    Some(some)
  } catch {
    case e : Exception => None
  }

  protected[vertx] def getOrNone[E](string : String, jsonObject: JsonObject)(some : => E) = if(jsonObject.containsKey(string)) {
    tryOrNone(some)
  } else {
    None
  }

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
        case exception: Exception => None
      }
    }
    def getAsStringSeq : Option[Seq[String]] = {
      val elems = json.size() - 1
      try {
        val objects = (0 to elems).map(json.getString)
        Some(objects)
      } catch {
        case exception: Exception => None
      }
    }
  }

  implicit class RichHttpServerResponse(response: HttpServerResponse) {

    def setResponse(response: (HttpCode, String)): Unit = setResponse(response._1.code, response._2)

    private def setResponse(statusCode: Int, obj: JsonObject): Unit = setResponse(statusCode, obj.encode())
    private def setResponse(statusCode: Int, body: String): Unit = {
      response.setStatusCode(statusCode).end(body)
    }

    def setInternalError(message: String = "Internal Error"): Unit = setResponse(500, message)
    def setNotFound(message: String = "Not Found"): Unit = setResponse(400, message)
    def setForbidden(message: String = "Forbidden"): Unit = setResponse(403, message)
    def setNotAuthorized (message: String = "Not authorized"): Unit = setResponse(401, message)
    def setBadRequest(message: String = "Bad request"): Unit = setResponse(400, message)
    def setConflict(message: String = "Conflict"): Unit = setResponse(409, message)
    def setCreated(obj: JsonObject): Unit = setResponse(201, obj)
    def setCreated(obj: String): Unit = setResponse(201, obj)
    def setOk (obj: JsonObject): Unit = setResponse(200, obj)
    def setOk (obj: JsonArray): Unit = setResponse(200, obj.encode())
    def setNoContent(): Unit = setResponse(204, "")
  }

  implicit class RichServerWebSocket(webSocket : ServerWebSocket) {
    def rejectInternalError(): Unit = webSocket.reject(500)
    def rejectForbidden(): Unit = webSocket.reject(403)
    def rejectNotAuthorized(): Unit = webSocket.reject(401)
    def rejectBadContent(): Unit = webSocket.reject(400)
    def writeTextJsonObject(json : JsonObject): ServerWebSocket = webSocket.writeTextMessage(json.encode())
    def writeTextJsonArray(json : JsonArray): ServerWebSocket = webSocket.writeTextMessage(json.encode())
  }
}
