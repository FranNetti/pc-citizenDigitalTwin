package it.unibo.cop_medic.view.frame

import java.awt.{Dimension, FlowLayout}

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.model.data.Roles.CopRole
import it.unibo.cop_medic.model.data.{Data, LeafCategory, Roles}
import it.unibo.cop_medic.util._
import it.unibo.cop_medic.view.View
import it.unibo.cop_medic.view.frame.panel.CitizenIdPanel
import javax.swing.border.EmptyBorder
import javax.swing.{BoxLayout, JCheckBox, JFrame, JPanel, JScrollPane}
import monix.execution.Scheduler

private [view] object PoliceFrame {

  def apply(title: String, controller: Controller) = new PoliceFrame(title, controller)

  private val WIDTH = 900
  private val HEIGHT = 400
  private val CITIZEN_FIELD_LENGTH = 400

  private val CITIZEN_ID_LABEL = "Citizen id to observe: "
  private val CITIZEN_ID_HINT = "Insert id..."
  private val CITIZEN_ID_BTN_RECEIVE = "RECEIVE UPDATES"
  private val CITIZEN_ID_BTN_STOP = "STOP ALL UPDATES"
  private val SELECT_ONE_LEAF_ERROR = "You have to select at least one category to be notified of!"
  private val EMPTY_TEXT_ERROR = "The user id must not be empty!"
  private val NOT_SUBSCRIBED_ERROR = "You didn't subscribed for updates from this user!"

  private case class LeafCategoryCheckBox(text: String, leafCategory: LeafCategory) extends JCheckBox(text.firstLetterUppercase)

}


private [view] class PoliceFrame(title: String, controller: Controller) extends JFrame {

  import PoliceFrame._

  private implicit val scheduler = Scheduler(View.defaultExecutionContext)
  private val leafCategoriesCheck = Roles.categoriesByRole(CopRole).map(x => LeafCategoryCheckBox(x.name, x.asInstanceOf[LeafCategory]))

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

  /* categories panel */
  private val checkboxLayout = new JPanel
  checkboxLayout setLayout new FlowLayout()
  leafCategoriesCheck.foreach(checkboxLayout.add)
  mainPanel add checkboxLayout

  /* table panel */
  val (table,tableModel) = createTable(Seq("user", "id", "category", "feeder", "date", "value"))
  val tablePane = new JScrollPane(table)
  mainPanel add tablePane

  setSize(WIDTH, HEIGHT)
  setResizable(false)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setLocationRelativeTo(null)
  setTitle(title)
  setVisible(false)

  /* elements reaction logic */
  private var informationMap: Map[String, List[Data]] = Map()

  citizenToObservePanel handleReceiveButton (user => {
    val list = leafCategoriesCheck.filter(_.isSelected).map(_.leafCategory)
    if(list.isEmpty) {
      citizenToObservePanel.field setText user
      showDialog(this, SELECT_ONE_LEAF_ERROR)
    }
    else {
      leafCategoriesCheck.foreach(_.setSelected(false))
      controller.subscribeTo(user, list.toSet).foreach { data =>
        val newList = if (informationMap contains user) {
          data +: informationMap(user).filterNot(_.category.name == data.category.name)
        } else List(data)
        informationMap = informationMap + (user -> newList)
        refreshTable()
      }
    }
  }, EMPTY_TEXT_ERROR)

  citizenToObservePanel handleStopButton (user => {
    if(informationMap contains user) {
      leafCategoriesCheck.foreach(_.setSelected(false))
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
