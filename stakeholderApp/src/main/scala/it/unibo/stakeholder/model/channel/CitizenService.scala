package it.unibo.stakeholder.model.channel

import java.net.URI

import io.vertx.lang.scala.json.JsonObject
import it.unibo.stakeholder.model.channel.parser.DataParserRegistry
import it.unibo.stakeholder.model.channel.rest.CitizenChannel

object CitizenService {

  /**
   * Create a CitizenService proxy client using the same interface of the service.
   * @param citizenId the id of the citizen
   * @param registry the data parser
   * @param address the address where the citizen service is located
   * @return CitizenService instance
   */
  def createProxy(citizenId: String, registry: DataParserRegistry[JsonObject], address: URI): CitizenChannel =
    new CitizenChannel(citizenId, registry, address)
}
