package it.unibo.stakeholder.view.frame.panel

import java.awt.Dimension

import it.unibo.stakeholder.view.frame._
import it.unibo.stakeholder.util._
import javax.swing.{BoxLayout, JButton, JLabel, JPanel, JTextField}

private[frame] object HistoryHeaderPanel {

  def apply(labelText: String, hintText: String, hintDim: Dimension,
            getHistoryText: String): HistoryHeaderPanel =
    new HistoryHeaderPanel(labelText, hintText, hintDim, getHistoryText)

}


private[frame] class HistoryHeaderPanel(labelText: String, hintText: String, hintDim: Dimension,
                                        getHistoryText: String) extends JPanel {

  private val label = new JLabel(labelText)
  private val receiveButton = new JButton(getHistoryText)
  val field: JTextField = createFieldWithHint(hintText, hintDim)

  this setLayout new BoxLayout(this, BoxLayout.LINE_AXIS)
  this add label
  this add createHorizontalBox()
  this add field
  this add createHorizontalBox()
  this add receiveButton

  def handleGetHistoryButton(fun: String => Unit)(emptyFieldError: String): Unit =
    receiveButton addActionListener (_ => {
      val user = field.getText
      if(user.hasWhiteSpaces) showDialog(getParent, emptyFieldError)
      else fun(user)
    })

}