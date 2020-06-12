package it.unibo.cop_medic.view

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.util.{LoginResult, SystemUser}
import javax.swing.SwingUtilities

import scala.concurrent.ExecutionContext

sealed trait LoginView {
  def loginSuccessful(userInfo: SystemUser)
  def loginFailed(error: String)
}

trait ViewController {
  def handleLoginResult(result: LoginResult)
}

trait View {
  def show()
}

object View {

  type ViewCreator = Controller => View

  def apply(): ViewCreator = ViewControllerImpl(_)

  val defaultExecutionContext: ExecutionContext = new SwingExecutionContext();

  class SwingExecutionContext extends ExecutionContext {
    override def execute(runnable: Runnable): Unit = SwingUtilities.invokeLater(runnable)
    override def reportFailure(cause: Throwable): Unit = throw cause
  }

}
