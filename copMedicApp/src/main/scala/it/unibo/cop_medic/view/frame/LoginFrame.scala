package it.unibo.cop_medic.view.frame

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.util
import it.unibo.cop_medic.view.{View, ViewController}
import javax.swing.border.EmptyBorder
import javax.swing._

import scala.util.{Failure, Success}

private[view] object LoginFrame {

  def apply(title: String, controller: Controller, viewController: ViewController) = new LoginFrame(title, controller, viewController)

  private val WIDTH = 325
  private val HEIGHT = 160

  private val LOGIN_BTN_TEXT = "Login"
  private val EMAIL_LABEL_TEXT = "Email"
  private val PWD_LABEL_TEXT = "Password"
  private val EMPTY_FIELD_ERROR = "The fields cannot be empty!"
  private val LOGIN_ERROR = "Error during login: "
}

/**
 * View for the login procedure.
 * @param title the view title
 * @param controller the main controller of the application
 * @param viewController the view controller of the application
 */
private[view] class LoginFrame(title: String, controller: Controller, viewController: ViewController) extends JFrame {

  import LoginFrame._

  private val emailField = createField()
  private val passwordField = createPasswordField()
  private val loginButton = new JButton(LOGIN_BTN_TEXT)
  private val emailLabel = createLabel(EMAIL_LABEL_TEXT)
  private val passwordLabel = createLabel(PWD_LABEL_TEXT)

  private val mainPanel = new JPanel
  mainPanel setBorder new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN)
  mainPanel setLayout new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)
  getContentPane add mainPanel

  private val emailPanel = new JPanel
  emailPanel setLayout new BoxLayout(emailPanel, BoxLayout.LINE_AXIS)
  emailPanel add emailLabel
  emailPanel add emailField
  mainPanel add emailPanel
  mainPanel add createVerticalBox()

  private val passwordPanel = new JPanel
  passwordPanel setLayout new BoxLayout(passwordPanel, BoxLayout.LINE_AXIS)
  passwordPanel add passwordLabel
  passwordPanel add passwordField
  mainPanel add passwordPanel
  mainPanel add createVerticalBox()

  private val loginButtonPanel = new JPanel
  loginButtonPanel add loginButton
  mainPanel add loginButtonPanel

  setSize(WIDTH, HEIGHT)
  setResizable(false)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setLocationRelativeTo(null)
  setTitle(title)
  setVisible(false)

  loginButton addActionListener ( _ => {
    import util._
    val password = passwordField.getPassword.mkString
    if (emailField.getText.hasNoWhiteSpaces && password.hasNoWhiteSpaces) {
      controller.doLogin(emailField.getText, password).onComplete {
        case Success(result) => viewController.handleLoginResult(result)
        case Failure(exception) => showDialog(this, LOGIN_ERROR + exception)
      }(View.defaultExecutionContext)
    } else {
     showDialog(this, EMPTY_FIELD_ERROR)
    }
  })

}
