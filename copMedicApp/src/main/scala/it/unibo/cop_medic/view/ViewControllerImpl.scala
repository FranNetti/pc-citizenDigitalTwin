package it.unibo.cop_medic.view

import it.unibo.cop_medic.controller.{Controller, FailedLogin, LoginResult, SuccessfulLogin, UnsupportedRole}
import it.unibo.cop_medic.model.data.Roles
import it.unibo.cop_medic.model.data.Roles._
import it.unibo.cop_medic.view.View.SwingExecutionContext
import it.unibo.cop_medic.view.frame.{LoginFrame, MedicFrame, PoliceFrame, showDialog}
import javax.swing.JFrame

/**
 * View that controls the other system's views.
 * @param controller the main controller of the application
 */
private[view] class ViewControllerImpl(controller: Controller) extends View with ViewController {

  import ViewControllerImpl._
  private val context = new SwingExecutionContext
  private val loginView = LoginFrame(TITLE, controller, this)
  private var applicationView: JFrame = loginView

  override def show(): Unit = context execute (() => loginView setVisible true)

  override def handleLoginResult(result: LoginResult): Unit = result match {
    case SuccessfulLogin(userInfo) =>
      applicationView =
        Roles.all.find(_.name == userInfo.role).map {
        case CopRole => PoliceFrame(POLICE_TITLE, controller, this)
        case MedicRole => MedicFrame(MEDIC_TITLE, controller, this)
      }.get
      loginView.setVisible(false)
      applicationView.setVisible(true)
    case FailedLogin(cause) => showDialog(loginView, LOGIN_ERROR_MESSAGE concat cause)
    case UnsupportedRole => showDialog(loginView, LOGIN_UNSUPPORTED_ROLE_MESSAGE)
  }

  override def showError(error: ViewError): Unit = error match {
    case SubscriptionFailed(error) => showDialog(applicationView, SUBSCRIPTION_ERROR concat error)
    case NotLoggedError => showDialog(applicationView, NOT_LOGGED_ERROR)
    case HistoryRequestFailed(error) =>  showDialog(applicationView, error)
  }

  override def showApplicationView(): Unit = applicationView setVisible true
}

private[view] object ViewControllerImpl {

  val TITLE = "CDT Application"
  val POLICE_TITLE = "Police Application"
  val MEDIC_TITLE = "Medic Application"
  private val LOGIN_ERROR_MESSAGE = "An error occurred while logging in: "
  private val LOGIN_UNSUPPORTED_ROLE_MESSAGE = "The role you have is not supported by this application!"
  private val SUBSCRIPTION_ERROR = "Error while subscribing: "
  private val NOT_LOGGED_ERROR = "You're not logged!"

  def apply(controller: Controller) = new ViewControllerImpl(controller)

}
