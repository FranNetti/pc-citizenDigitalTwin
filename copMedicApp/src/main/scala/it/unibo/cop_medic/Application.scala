package it.unibo.cop_medic

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.view.View

object Application extends App {

  val authenticationHost = "http://localhost:8081"
  val serviceHost = "http://localhost:8080"

  val context = View.defaultExecutionContext
  val view = View()

  val controller = Controller(view, authenticationHost, serviceHost, context)

}
