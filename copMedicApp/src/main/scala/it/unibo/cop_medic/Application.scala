package it.unibo.cop_medic

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.view.View

/**
 * Main entry of the application.
 */
object Application extends App {

  val authenticationHost = "151.42.236.72"
  val serviceHost = "151.42.236.72"

  val view = View()

  val controller = Controller(view, authenticationHost, serviceHost)

}
