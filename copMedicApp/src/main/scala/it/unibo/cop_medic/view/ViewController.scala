package it.unibo.cop_medic.view

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.util
import javax.swing.JOptionPane

private[view] class ViewController(controller: Controller) extends View {

  import ViewController._
  private val loginView = LoginFrame(TITLE, controller)

  override def show(): Unit = loginView setVisible true

  override def loginSuccessful(userInfo: util.SystemUser): Unit = println(userInfo)

  override def loginFailed(error: String): Unit =
    JOptionPane.showMessageDialog(loginView, LOGIN_ERROR_MESSAGE + error)

}

object ViewController {

  val TITLE = "CDT Application"
  private val LOGIN_ERROR_MESSAGE = "An error occurred while logging in: "

  def apply(controller: Controller) = new ViewController(controller)

}
