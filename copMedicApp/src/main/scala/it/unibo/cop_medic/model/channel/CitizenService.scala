package it.unibo.cop_medic.model.channel

import io.vertx.lang.scala.json.JsonObject
import it.unibo.cop_medic.model.channel.parser.DataParserRegistry
import it.unibo.cop_medic.model.channel.rest.CitizenChannel

object CitizenService {
  def createProxy(citizenId: String, registry: DataParserRegistry[JsonObject], host: String = "localhost") =
    new CitizenChannel(citizenId, registry, host = host)
}
