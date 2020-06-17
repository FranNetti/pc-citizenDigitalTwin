package it.unibo.cop_medic.view

import it.unibo.cop_medic.controller.{Controller, LoginResult}
import javax.swing.SwingUtilities

import scala.concurrent.ExecutionContext

/**
 * Trait for classes that control other views.
 */
trait ViewController {
  /**
   * handle the result of the login operation.
   * @param result the result of the login operation
   */
  def handleLoginResult(result: LoginResult)
}

/**
 * Trait for classes that want to implement an UI.
 */
trait View {
  /**
   * Show the main view.
   */
  def show()

  /**
   * Show an error.
   * @param error the error to show
   */
  def showError(error: ViewError)
}

/**
 * Trait for classes that represent an error that has to be shown on the view.
 */
sealed trait ViewError

/**
 * An error about the subscription to a certain category occured.
 * @param error the error
 */
case class SubscriptionFailed(error: String) extends ViewError

object View {

  type ViewCreator = Controller => View

  def apply(): ViewCreator = ViewControllerImpl(_)

  private[view] val defaultExecutionContext: ExecutionContext = new SwingExecutionContext();

  private[view] class SwingExecutionContext extends ExecutionContext {
    override def execute(runnable: Runnable): Unit = SwingUtilities.invokeLater(runnable)
    override def reportFailure(cause: Throwable): Unit = throw cause
  }

}
