package it.unibo.stakeholder.view.frame.panel

import java.awt.Dimension

import it.unibo.stakeholder.view.frame._
import it.unibo.stakeholder.util._
import javax.swing.{BoxLayout, JButton, JLabel, JPanel, JTextField}

private[frame] object CitizenIdPanel {

  def apply(labelText: String, hintText: String, hintDim: Dimension,
                                   receiveButtonText: String, stopButtonText: String): CitizenIdPanel =
    new CitizenIdPanel(labelText, hintText, hintDim, receiveButtonText, stopButtonText)

}


private[frame] class CitizenIdPanel(labelText: String, hintText: String, hintDim: Dimension,
                                          receiveButtonText: String, stopButtonText: String) extends JPanel {

  private val label = new JLabel(labelText)
  private val receiveButton = new JButton(receiveButtonText)
  private val stopButton = new JButton(stopButtonText)
  val field: JTextField = createFieldWithHint(hintText, hintDim)

  this setLayout new BoxLayout(this, BoxLayout.LINE_AXIS)
  this add label
  this add createHorizontalBox()
  this add field
  this add createHorizontalBox()
  this add receiveButton
  this add createHorizontalBox()
  this add stopButton

  def handleReceiveButton(fun: String => Unit)(emptyFieldError: String): Unit =
    receiveButton addActionListener (_ => handleBtnClick(fun, emptyFieldError))

  def handleStopButton(fun: String => Unit)(emptyFieldError: String): Unit =
    stopButton addActionListener (_ => handleBtnClick(fun, emptyFieldError))

  private def handleBtnClick(fun: String => Unit, emptyFieldError: String): Unit = {
    val user = field.getText
    if(user.hasWhiteSpaces) showDialog(getParent, emptyFieldError)
    else {
      field.setText("")
      fun(user)
    }
  }

}