package it.unibo.cop_medic.view

import java.awt.{Component, Dimension}
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.JavaConverters._

import it.unibo.cop_medic.model.data.{Data, LeafCategory, Resource, Sensor}
import javax.swing.table.DefaultTableModel
import javax.swing.{Box, JComboBox, JLabel, JList, JOptionPane, JPasswordField, JTable, JTextField, ListCellRenderer}

package object frame {

  val MARGIN = 10
  val TEXT_HEIGHT = 30

  private val BOX_HEIGHT = 5
  private val BOX_WIDTH = 5
  private val LABEL_DIM = new Dimension(100, TEXT_HEIGHT)
  private val FIELD_DIM = new Dimension(200, TEXT_HEIGHT)

  def createLabel(text: String, labelDim: Dimension = LABEL_DIM): JLabel =
    new JLabel(text) setComponentSize labelDim

  def createField(fieldDim: Dimension = FIELD_DIM): JTextField =
    new JTextField() setComponentSize fieldDim

  def createPasswordField(fieldDim: Dimension = FIELD_DIM): JPasswordField =
    new JPasswordField() setComponentSize fieldDim

  def createVerticalBox(boxHeight: Int = BOX_HEIGHT): Component = Box.createVerticalStrut(boxHeight)

  def createHorizontalBox(boxWidth: Int = BOX_WIDTH): Component = Box.createHorizontalStrut(boxWidth)

  def createFieldWithHint(hintText: String, fieldDim: Dimension = FIELD_DIM): JTextField =
    new HintTextField(hintText) setComponentSize fieldDim

  def showDialog(parent: Component, text: String): Unit = JOptionPane.showMessageDialog(parent, text)

  def createTable(columns: Seq[String]): (JTable, DefaultTableModel) = {
    val tableModel = new DefaultTableModel()
    columns.foreach{tableModel.addColumn}
    val table = new JTable(tableModel)
    table setFillsViewportHeight true
    (table, tableModel)
  }

  def createComboBox(choices: Set[LeafCategory], dimension: Dimension): JComboBox[LeafCategory] = {
    val comboBox = new JComboBox(choices.toArray)
    comboBox.setRenderer(ComboBoxRenderer())
    comboBox setComponentSize dimension
  }

  def toTableFormat(info:(String,Data)) : Array[AnyRef] = {
    val feeder = info._2.feeder match {
      case Resource(uri) => uri
      case Sensor(name) => name
    }
    val valueFormat =  info._2.value match {
      case it : Iterable[_] => it.mkString(";")
      case other => other.toString
    }
    val date = timestampToDate(info._2.timestamp)
    Seq(info._1, info._2.identifier, info._2.category.name, feeder, date, valueFormat).asJava.toArray
  }

  private val DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

  def timestampToDate(timestamp: Long): String = {
    val time = new Timestamp(timestamp)
    DATE_FORMATTER.format(new Date(time.getTime))
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

  private case class ComboBoxRenderer() extends JLabel with ListCellRenderer[LeafCategory] {
    override def getListCellRendererComponent(
                   list: JList[_ <: LeafCategory],
                   value: LeafCategory,
                   index: Int,
                   isSelected: Boolean,
                   cellHasFocus: Boolean): Component = {
      setText(value.name)
      this
    }
  }

  implicit class RichComponent[X <: Component](component: X) {
    def setComponentSize(dimension: Dimension): X = {
      component.setMaximumSize(dimension)
      component.setMinimumSize(dimension)
      component.setPreferredSize(dimension)
      component.setSize(dimension)
      component
    }
  }

}
