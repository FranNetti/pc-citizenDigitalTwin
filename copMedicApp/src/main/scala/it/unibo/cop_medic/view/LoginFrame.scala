package it.unibo.cop_medic.view

import java.awt.Dimension

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.util
import javax.swing.{Box, BoxLayout, JButton, JFrame, JLabel, JPanel, JOptionPane, JTextField}
import javax.swing.border.EmptyBorder

private[view] object LoginFrame {

  def apply(title: String, controller: Controller) = new LoginFrame(title, controller)

  private val MARGIN = 10
  private val WIDTH = 325
  private val HEIGHT = 160
  private val TEXT_HEIGHT = 30
  private val BOX_HEIGHT = 5

  private val LABEL_DIM = new Dimension(100, TEXT_HEIGHT)
  private val FIELD_DIM = new Dimension(200, TEXT_HEIGHT)

  private val LOGIN_BTN_TEXT = "Login"
  private val EMAIL_LABEL_TEXT = "Email"
  private val PWD_LABEL_TEXT = "Password"
  private val EMPTY_FIELD_ERROR = "The fields cannot be empty!"

  private def createLabel(text: String) = {
    val label = new JLabel(text)
    label setSize LABEL_DIM
    label setPreferredSize LABEL_DIM
    label setMinimumSize LABEL_DIM
    label setMaximumSize LABEL_DIM
    label
  }

  private def createField() = {
    val field = new JTextField()
    field setSize FIELD_DIM
    field setPreferredSize FIELD_DIM
    field setMinimumSize FIELD_DIM
    field setMaximumSize FIELD_DIM
    field
  }

  private def createVerticalBox() = Box.createVerticalStrut(BOX_HEIGHT)

}

private[view] class LoginFrame(title: String, controller: Controller) extends JFrame {

  import LoginFrame._

  private val emailField = createField()
  private val passwordField = createField()
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

  loginButton addActionListener ( _ => {
    import util._
    if (emailField.getText.hasNoWhiteSpaces && passwordField.getText.hasNoWhiteSpaces) {
      controller.doLogin(emailField.getText, passwordField.getText)
    } else {
      JOptionPane.showMessageDialog(this, EMPTY_FIELD_ERROR)
    }
  })

}
