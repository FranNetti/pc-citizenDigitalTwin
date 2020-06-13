package it.unibo.cop_medic.view.frame

import java.awt.Dimension

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.model.data.Roles.MedicRole
import it.unibo.cop_medic.model.data.{Categories, Data, LeafCategory, Roles}
import it.unibo.cop_medic.util._
import it.unibo.cop_medic.view.View
import javax.swing.border.EmptyBorder
import javax.swing.{BoxLayout, JButton, JFrame, JLabel, JPanel, JScrollPane}
import monix.execution.Scheduler

private [view] object MedicFrame {

  def apply(title: String, controller: Controller) = new MedicFrame(title, controller)

  private val WIDTH = 900
  private val HEIGHT = 480
  private val CITIZEN_FIELD_LENGTH = 400
  private val ADD_DATA_CIT_FIELD_LENGTH = 150
  private val ADD_DATA_VALUE_FIELD_LENGTH = 100
  private val ADD_DATA_CAT_FIELD_LENGTH = 150

  private val CITIZEN_ID_LABEL = "Citizen id to observe: "
  private val CITIZEN_ID_HINT = "Insert id..."
  private val CITIZEN_ID_BTN_RECEIVE = "RECEIVE UPDATES"
  private val CITIZEN_ID_BTN_STOP = "STOP UPDATES"
  private val EMPTY_TEXT_ERROR = "The user id must not be empty!"
  private val NOT_SUBSCRIBED_ERROR = "You didn't subscribed for updates from this user!"

  private val ADD_DATA_CIT_LABEL = "Citizen id"
  private val ADDA_DATA_VAL_LABEL = "Value"
  private val ADD_DATA_CAT_LABEL = "Data category"
  private val ADD_DATA_BTN_GENERATE = "GENERATE DATA"

}


private [view] class MedicFrame(title: String, controller: Controller) extends JFrame {

  import MedicFrame._

  private implicit val scheduler = Scheduler(View.defaultExecutionContext)
  private val categoriesToObserve = Roles.categoriesByRole(MedicRole)
  private val categoriesToChange = Categories.medicalDataCategory.dataCategory.map(_.asInstanceOf[LeafCategory])

  private val citizenIdLabel = new JLabel(CITIZEN_ID_LABEL)
  private val citizenIdField = createFieldWithHint(CITIZEN_ID_HINT, new Dimension(CITIZEN_FIELD_LENGTH, TEXT_HEIGHT))
  private val citizenIdReceiveButton = new JButton(CITIZEN_ID_BTN_RECEIVE)
  private val citizenIdStopButton = new JButton(CITIZEN_ID_BTN_STOP)

  private val addDataCitLabel = new JLabel(ADD_DATA_CIT_LABEL)
  private val addDataValLabel = new JLabel(ADDA_DATA_VAL_LABEL)
  private val addDataCatLabel = new JLabel(ADD_DATA_CAT_LABEL)
  private val addDataCitField = createFieldWithHint(CITIZEN_ID_HINT, new Dimension(ADD_DATA_CIT_FIELD_LENGTH, TEXT_HEIGHT))
  private val addDataValField = createField(new Dimension(ADD_DATA_VALUE_FIELD_LENGTH, TEXT_HEIGHT))
  private val addDataCatChoice = createComboBox(categoriesToChange, new Dimension(ADD_DATA_CAT_FIELD_LENGTH, TEXT_HEIGHT))
  private val addDataButton = new JButton(ADD_DATA_BTN_GENERATE)

  /* Main panel */
  private val mainPanel = new JPanel
  mainPanel setBorder new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN)
  mainPanel setLayout new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)
  getContentPane add mainPanel

  /* citizen to observe panel */
  private val citizenToObservePanel = new JPanel
  citizenToObservePanel setLayout new BoxLayout(citizenToObservePanel, BoxLayout.LINE_AXIS)
  citizenToObservePanel add citizenIdLabel
  citizenToObservePanel add createHorizontalBox()
  citizenToObservePanel add citizenIdField
  citizenToObservePanel add createHorizontalBox()
  citizenToObservePanel add citizenIdReceiveButton
  citizenToObservePanel add createHorizontalBox()
  citizenToObservePanel add citizenIdStopButton
  mainPanel add citizenToObservePanel

  /* table panel */
  val (tableModel,table) = createTable(Seq("user", "id", "category", "feeder", "date", "value"))
  val tablePane = new JScrollPane(table)
  table setFillsViewportHeight true
  mainPanel add createVerticalBox()
  mainPanel add tablePane
  tablePane.setMaximumSize(new Dimension(850, 300))
  tablePane.setMinimumSize(new Dimension(850, 300))
  tablePane.setPreferredSize(new Dimension(850, 300))
  tablePane.setSize(new Dimension(850, 300))

  private val addDataPanel = new JPanel
  addDataPanel setLayout new BoxLayout(addDataPanel, BoxLayout.LINE_AXIS)
  addDataPanel add addDataCitLabel
  addDataPanel add createHorizontalBox()
  addDataPanel add addDataCitField
  addDataPanel add createHorizontalBox()
  addDataPanel add addDataValLabel
  addDataPanel add createHorizontalBox()
  addDataPanel add addDataValField
  addDataPanel add createHorizontalBox()
  addDataPanel add addDataCatLabel
  addDataPanel add createHorizontalBox()
  addDataPanel add addDataCatChoice
  addDataPanel add createHorizontalBox(10)
  addDataPanel add addDataButton
  mainPanel add addDataPanel

  setSize(WIDTH, HEIGHT)
  setResizable(false)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setLocationRelativeTo(null)
  setTitle(title)
  setVisible(false)

  /* elements reaction logic */
  private var informationMap: Map[String, List[Data]] = Map()

  citizenIdReceiveButton addActionListener ( _ => {
    handleBtnClick((user, categories) => {
      citizenIdField.setText("")
      controller.subscribeTo(user, categories).foreach { data =>
        val newList = if (informationMap contains user) {
          data +: informationMap(user).filterNot(_.category.name == data.category.name)
        } else List(data)
        informationMap = informationMap + (user -> newList)
        refreshTable()
      }
    })
  })

  citizenIdStopButton addActionListener ( _ => {
    handleBtnClick((user, _) => {
      if(informationMap contains user) {
        citizenIdField.setText("")
        controller unsubscribeFrom user
      } else {
        showDialog(this, NOT_SUBSCRIBED_ERROR)
      }
    })
  })

  private def handleBtnClick(onSuccess: (String, Set[LeafCategory]) => Unit): Unit = {
    val user = citizenIdField.getText
    if(user.hasWhiteSpaces) showDialog(this, EMPTY_TEXT_ERROR)
    else onSuccess(user, categoriesToObserve.map(_.asInstanceOf[LeafCategory]).toSet)
  }

  private def refreshTable(): Unit = {
    val newList = informationMap.toSeq.sortBy(_._1).flatMap(x => {
      for(elem <- x._2) yield (x._1, elem)
    }).map(toTableFormat)
    for(i <- 0 until tableModel.getRowCount) {
      tableModel.removeRow(0)
    }
    newList.foreach(tableModel.addRow)
  }

}