package it.unibo.cop_medic.view.frame

import java.awt.Dimension

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.model.data.Roles.MedicRole
import it.unibo.cop_medic.model.data.{Categories, Data, LeafCategory, Roles}
import it.unibo.cop_medic.view.View
import it.unibo.cop_medic.view.frame.panel.{AddDataPanel, CitizenIdPanel}
import javax.swing.border.EmptyBorder
import javax.swing.{BoxLayout, JFrame, JPanel, JScrollPane}
import monix.execution.Scheduler

private [view] object MedicFrame {

  def apply(title: String, controller: Controller) = new MedicFrame(title, controller)

  private val WIDTH = 900
  private val HEIGHT = 480
  private val CITIZEN_FIELD_LENGTH = 400
  private val ADD_DATA_CIT_FIELD_LENGTH = 150
  private val ADD_DATA_VALUE_FIELD_LENGTH = 100
  private val ADD_DATA_CAT_FIELD_LENGTH = 150
  private val TABLE_WIDTH = 850
  private val TABLE_HEIGHT = 300

  private val CITIZEN_ID_LABEL = "Citizen id to observe: "
  private val CITIZEN_ID_HINT = "Insert id..."
  private val CITIZEN_ID_BTN_RECEIVE = "RECEIVE UPDATES"
  private val CITIZEN_ID_BTN_STOP = "STOP UPDATES"
  private val EMPTY_TEXT_ERROR = "The user id must not be empty!"
  private val NOT_SUBSCRIBED_ERROR = "You didn't subscribed for updates from this user!"
}


private [view] class MedicFrame(title: String, controller: Controller) extends JFrame {

  import MedicFrame._

  private implicit val scheduler = Scheduler(View.defaultExecutionContext)
  private val categoriesToObserve = Roles.categoriesByRole(MedicRole).map(_.asInstanceOf[LeafCategory]).toSet
  private val categoriesToChange = Categories.medicalDataCategory.dataCategory.map(_.asInstanceOf[LeafCategory])

  /* Main panel */
  private val mainPanel = new JPanel
  mainPanel setBorder new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN)
  mainPanel setLayout new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)
  getContentPane add mainPanel

  /* citizen to observe panel */
  private val citizenToObservePanel = CitizenIdPanel(
    CITIZEN_ID_LABEL, CITIZEN_ID_HINT, new Dimension(CITIZEN_FIELD_LENGTH, TEXT_HEIGHT),
    CITIZEN_ID_BTN_RECEIVE, CITIZEN_ID_BTN_STOP
  )
  mainPanel add citizenToObservePanel

  /* table panel */
  private val (table,tableModel) = createTable(Seq("user", "id", "category", "feeder", "date", "value"))
  private val tablePane = new JScrollPane(table) setComponentSize new Dimension(TABLE_WIDTH, TABLE_HEIGHT)
  mainPanel add createVerticalBox()
  mainPanel add tablePane

  private val addDataPanel = AddDataPanel(
    categoriesToChange, new Dimension(ADD_DATA_CIT_FIELD_LENGTH, TEXT_HEIGHT),
    new Dimension(ADD_DATA_VALUE_FIELD_LENGTH, TEXT_HEIGHT), new Dimension(ADD_DATA_CAT_FIELD_LENGTH, TEXT_HEIGHT)
  )
  mainPanel add addDataPanel

  setSize(WIDTH, HEIGHT)
  setResizable(false)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setLocationRelativeTo(null)
  setTitle(title)
  setVisible(false)

  /* elements reaction logic */
  private var informationMap: Map[String, List[Data]] = Map()

  citizenToObservePanel handleReceiveButton (user => {
    controller.subscribeTo(user, categoriesToObserve).foreach { data =>
      val newList = if (informationMap contains user) {
        data +: informationMap(user).filterNot(_.category.name == data.category.name)
      } else List(data)
      informationMap = informationMap + (user -> newList)
      refreshTable()
    }
  }, EMPTY_TEXT_ERROR)

  citizenToObservePanel handleStopButton (user => {
    if(informationMap contains user) {
      controller unsubscribeFrom user
    } else {
      showDialog(this, NOT_SUBSCRIBED_ERROR)
    }
  }, EMPTY_TEXT_ERROR)

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