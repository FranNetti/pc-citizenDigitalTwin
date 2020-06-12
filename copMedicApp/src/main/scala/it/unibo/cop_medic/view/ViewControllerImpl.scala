package it.unibo.cop_medic.view

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.model.data.Roles
import it.unibo.cop_medic.model.data.Roles._
import it.unibo.cop_medic.util.{FailedLogin, LoginResult, SuccessfulLogin, UnsupportedRole}
import it.unibo.cop_medic.view.View.SwingExecutionContext
import it.unibo.cop_medic.view.frame.{LoginFrame, PoliceFrame}
import javax.swing.{JFrame, JOptionPane}

private[view] class ViewControllerImpl(controller: Controller) extends View with ViewController {

  import ViewControllerImpl._
  private val context = new SwingExecutionContext
  private val loginView = LoginFrame(TITLE, controller, this)

  override def show(): Unit = context execute (() => loginView setVisible true)

  override def handleLoginResult(result: LoginResult): Unit = result match {
    case SuccessfulLogin(userInfo) =>
      val view: JFrame =
        Roles.all.find(_.name == userInfo.role).map({
        case CopRole => PoliceFrame(POLICE_TITLE, controller)
        case MedicRole => null
      }).get
      loginView.setVisible(false)
      view.setVisible(true)
    case FailedLogin(cause) => JOptionPane.showMessageDialog(loginView, LOGIN_ERROR_MESSAGE + cause)
    case UnsupportedRole => JOptionPane.showMessageDialog(loginView, LOGIN_UNSUPPORTED_ROLE_MESSAGE)
  }
}

object ViewControllerImpl {

  val TITLE = "CDT Application"
  val POLICE_TITLE = "Police Application"
  private val LOGIN_ERROR_MESSAGE = "An error occurred while logging in: "
  private val LOGIN_UNSUPPORTED_ROLE_MESSAGE = "The role you have is not supported by this application!"

  def apply(controller: Controller) = new ViewControllerImpl(controller)

}
