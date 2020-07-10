package it.unibo.stakeholder

import java.net.URI

import io.vertx.lang.scala.json.JsonObject
import it.unibo.stakeholder.controller.Controller
import it.unibo.stakeholder.view.View

import scala.io.Source

/**
 * Main entry of the application.
 */
object Application extends App {

  val jsonConfigurations = new JsonObject(Source.fromResource("configurations.json").mkString)

  val authenticationHost = jsonConfigurations.getString("authenticationHost")
  val authenticationPort = jsonConfigurations.getInteger("authenticationPort")
  val citizenHost = jsonConfigurations.getString("citizenHost")
  val citizenPort = jsonConfigurations.getInteger("citizenPort")

  val authenticationAddress = URI.create(s"http://${authenticationHost}:${authenticationPort}")
  val citizenAddress = URI.create(s"http://${citizenHost}:${citizenPort}")

  val view = View()

  val controller = Controller(view, authenticationAddress, citizenAddress)

}
