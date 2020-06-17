package it.unibo.cop_medic.model.channel

import io.vertx.lang.scala.json.JsonObject
import it.unibo.cop_medic.model.channel.parser.DataParserRegistry
import it.unibo.cop_medic.model.channel.rest.CitizenChannel

object CitizenService {

  /**
   * Create a CitizenService proxy client using the same interface of the service.
   * @param citizenId the id of the citizen
   * @param registry the data parser
   * @param host the host where the citizen service is located
   * @return CitizenService instance
   */
  def createProxy(citizenId: String, registry: DataParserRegistry[JsonObject], host: String = "localhost"): CitizenChannel =
    new CitizenChannel(citizenId, registry, host = host)
}
