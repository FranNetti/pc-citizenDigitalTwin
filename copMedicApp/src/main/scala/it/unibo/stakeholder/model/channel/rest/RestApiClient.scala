package it.unibo.stakeholder.model.channel.rest

import io.vertx.scala.ext.web.client.WebClient

/**
 * Abstraction for a generic rest api client based on vertx webclient.
 */
trait RestApiClient {
  def webClient: WebClient
}
