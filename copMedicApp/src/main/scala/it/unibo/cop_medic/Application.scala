package it.unibo.cop_medic

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.view.View

object Application extends App {

  val authenticationHost = "localhost"
  val serviceHost = "localhost"

  val context = Controller.defaultExecutionContext
  val view = View()

  val controller = Controller(view, authenticationHost, serviceHost, context)

}
