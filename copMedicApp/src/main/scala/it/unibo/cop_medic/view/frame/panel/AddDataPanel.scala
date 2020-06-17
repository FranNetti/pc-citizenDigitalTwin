package it.unibo.cop_medic.view.frame.panel

import java.awt.Dimension

import it.unibo.cop_medic.model.data.LeafCategory
import it.unibo.cop_medic.view.frame._
import it.unibo.cop_medic.util._
import javax.swing.{BoxLayout, JButton, JLabel, JPanel}

private[frame] object AddDataPanel {

  def apply(categories: Set[LeafCategory], citizenFieldWidth: Dimension,
            dataFieldWidth: Dimension, categoriesFieldWidth: Dimension): AddDataPanel =
    new AddDataPanel(categories, citizenFieldWidth, dataFieldWidth, categoriesFieldWidth)

  private val ADD_DATA_CIT_LABEL = "Citizen id"
  private val ADDA_DATA_VAL_LABEL = "Value"
  private val ADD_DATA_CAT_LABEL = "Data category"
  private val ADD_DATA_BTN_GENERATE = "GENERATE DATA"
  private val ADD_DATA_CIT_HINT = "Insert id..."

}


private[frame] class AddDataPanel(categories: Set[LeafCategory], citizenFieldWidth: Dimension,
                                  dataFieldWidth: Dimension, categoriesFieldWidth: Dimension) extends JPanel {

  import AddDataPanel._

  private val citizenLabel = new JLabel(ADD_DATA_CIT_LABEL)
  private val valueLabel = new JLabel(ADDA_DATA_VAL_LABEL)
  private val categoryLabel = new JLabel(ADD_DATA_CAT_LABEL)
  private val citizenField = createFieldWithHint(ADD_DATA_CIT_HINT, citizenFieldWidth)
  private val valueField = createField(dataFieldWidth)
  private val categoryChoice = createComboBox(categories, categoriesFieldWidth)
  private val addDataButton = new JButton(ADD_DATA_BTN_GENERATE)

  this setLayout new BoxLayout(this, BoxLayout.LINE_AXIS)
  this add citizenLabel
  this add createHorizontalBox()
  this add citizenField
  this add createHorizontalBox()
  this add valueLabel
  this add createHorizontalBox()
  this add valueField
  this add createHorizontalBox()
  this add categoryLabel
  this add createHorizontalBox()
  this add categoryChoice
  this add createHorizontalBox(10)
  this add addDataButton

  def handleAddNewData(fun: (String, String, LeafCategory) => Unit)(emptyCitizenError: String, emptyValueError: String): Unit =
    addDataButton addActionListener (_ => {
      val citizenId = citizenField.getText
      val value = valueField.getText
      val category = categoryChoice.getSelectedItem.asInstanceOf[LeafCategory]
      if(citizenId.hasWhiteSpaces) showDialog(getParent, emptyCitizenError)
      else if(value.hasWhiteSpaces) showDialog(getParent, emptyValueError)
      else {
        valueField setText ""
        fun(citizenId, value, category)
      }
    })

}
