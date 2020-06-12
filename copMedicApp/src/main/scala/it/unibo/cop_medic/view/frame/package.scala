package it.unibo.cop_medic.view

import java.awt.{Component, Dimension}
import java.awt.event.FocusEvent
import java.awt.event.FocusListener

import javax.swing.{Box, JLabel, JOptionPane, JPasswordField, JTextField}

package object frame {

  val MARGIN = 10
  val TEXT_HEIGHT = 30

  private val BOX_HEIGHT = 5
  private val BOX_WIDTH = 5
  private val LABEL_DIM = new Dimension(100, TEXT_HEIGHT)
  private val FIELD_DIM = new Dimension(200, TEXT_HEIGHT)

  def createLabel(text: String, labelDim: Dimension = LABEL_DIM): JLabel = {
    val label = new JLabel(text)
    label setSize labelDim
    label setPreferredSize labelDim
    label setMinimumSize labelDim
    label setMaximumSize labelDim
    label
  }

  def createField(fieldDim: Dimension = FIELD_DIM): JTextField = {
    val field = new JTextField()
    field setSize fieldDim
    field setPreferredSize fieldDim
    field setMinimumSize fieldDim
    field setMaximumSize fieldDim
    field
  }

  def createPasswordField(fieldDim: Dimension = FIELD_DIM): JPasswordField = {
    val field = new JPasswordField()
    field setSize fieldDim
    field setPreferredSize fieldDim
    field setMinimumSize fieldDim
    field setMaximumSize fieldDim
    field
  }

  def createVerticalBox(boxHeight: Int = BOX_HEIGHT): Component = Box.createVerticalStrut(boxHeight)

  def createHorizontalBox(boxWidth: Int = BOX_WIDTH): Component = Box.createHorizontalStrut(boxWidth)

  def createFieldWithHint(hintText: String, fieldDim: Dimension = FIELD_DIM): JTextField = {
    val field = new HintTextField(hintText)
    field setSize fieldDim
    field setPreferredSize fieldDim
    field setMinimumSize fieldDim
    field setMaximumSize fieldDim
    field
  }

  private class HintTextField(hint: String) extends JTextField(hint) with FocusListener {

    var showingHint = true
    super.addFocusListener(this)

    override def focusGained(e: FocusEvent): Unit = {
      if (this.getText.isEmpty) {
        super.setText("")
        showingHint = false
      }
    }

    override def focusLost(e: FocusEvent): Unit = {
      if (this.getText.isEmpty) {
        super.setText(hint)
        showingHint = true
      }
    }

    override def getText: String = if (showingHint) "" else super.getText
  }

  def showDialog(parent: Component, text: String): Unit = JOptionPane.showMessageDialog(parent, text)

}
