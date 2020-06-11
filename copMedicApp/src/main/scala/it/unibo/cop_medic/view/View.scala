package it.unibo.cop_medic.view

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.util.SystemUser
import javax.swing.SwingUtilities

import scala.concurrent.ExecutionContext

sealed trait LoginView {
  def loginSuccessful(userInfo: SystemUser)
  def loginFailed(error: String)
}

trait View extends LoginView {
  def show()
}

object View {

  type ViewCreator = Controller => View

  def apply(): ViewCreator = ViewController(_)

  val defaultExecutionContext: ExecutionContext = new SwingExecutionContext();

  private class SwingExecutionContext extends ExecutionContext {
    override def execute(runnable: Runnable): Unit = SwingUtilities.invokeLater(runnable)
    override def reportFailure(cause: Throwable): Unit = throw cause
  }

}
