package it.unibo.cop_medic.view.frame.panel

import java.awt.Dimension

import it.unibo.cop_medic.model.data.LeafCategory
import it.unibo.cop_medic.view.frame._
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

  private val addDataCitLabel = new JLabel(ADD_DATA_CIT_LABEL)
  private val addDataValLabel = new JLabel(ADDA_DATA_VAL_LABEL)
  private val addDataCatLabel = new JLabel(ADD_DATA_CAT_LABEL)
  private val addDataCitField = createFieldWithHint(ADD_DATA_CIT_HINT, citizenFieldWidth)
  private val addDataValField = createField(dataFieldWidth)
  private val addDataCatChoice = createComboBox(categories, categoriesFieldWidth)
  private val addDataButton = new JButton(ADD_DATA_BTN_GENERATE)

  this setLayout new BoxLayout(this, BoxLayout.LINE_AXIS)
  this add addDataCitLabel
  this add createHorizontalBox()
  this add addDataCitField
  this add createHorizontalBox()
  this add addDataValLabel
  this add createHorizontalBox()
  this add addDataValField
  this add createHorizontalBox()
  this add addDataCatLabel
  this add createHorizontalBox()
  this add addDataCatChoice
  this add createHorizontalBox(10)
  this add addDataButton

}
