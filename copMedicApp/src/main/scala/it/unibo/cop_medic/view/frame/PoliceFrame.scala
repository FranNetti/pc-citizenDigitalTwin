package it.unibo.cop_medic.view.frame

import java.awt.{Dimension, FlowLayout}
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import scala.collection.JavaConverters._
import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.model.data.Roles.CopRole
import it.unibo.cop_medic.model.data.{Data, LeafCategory, Resource, Roles, Sensor}
import it.unibo.cop_medic.util._
import it.unibo.cop_medic.view.View
import javax.swing.border.EmptyBorder
import javax.swing.table.DefaultTableModel
import javax.swing.{BoxLayout, JButton, JCheckBox, JFrame, JLabel, JPanel, JScrollPane, JTable}
import monix.execution.{CancelableFuture, Scheduler}

private [view] object PoliceFrame {

  def apply(title: String, controller: Controller) = new PoliceFrame(title, controller)

  private val WIDTH = 900
  private val HEIGHT = 400
  private val CITIZEN_FIELD_LENGTH = 400

  private val CITIZEN_ID_LABEL = "Citizen id to observe: "
  private val CITIZEN_ID_HINT = "Insert id..."
  private val CITIZEN_ID_BTN_RECEIVE = "RECEIVE UPDATES"
  private val CITIZEN_ID_BTN_STOP = "STOP UPDATES"
  private val SELECT_ONE_LEAF_ERROR = "You have to select at least one category to be notified of!"
  private val EMPTY_TEXT_ERROR = "The user id must not be empty!"
  private val NOT_SUBSCRIBED_ERROR = "You didn't subscribed for updates from this user!"

  private def toTableFormat(info:(String,Data)) : Array[AnyRef] = {
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
  private def timestampToDate(timestamp: Long): String = {
    val time = new Timestamp(timestamp)
    DATE_FORMATTER.format(new Date(time.getTime))
  }

  private case class LeafCategoryCheckBox(text: String, leafCategory: LeafCategory) extends JCheckBox(text.firstLetterUppercase)

}


private [view] class PoliceFrame(title: String, controller: Controller) extends JFrame {

  import PoliceFrame._

  private implicit val scheduler = Scheduler(View.defaultExecutionContext)
  private val leafCategoriesCheck = Roles.categoriesByRole(CopRole).map(x => LeafCategoryCheckBox(x.name, x.asInstanceOf[LeafCategory]))

  private val citizenIdLabel = new JLabel(CITIZEN_ID_LABEL)
  private val citizenIdField = createFieldWithHint(CITIZEN_ID_HINT, new Dimension(CITIZEN_FIELD_LENGTH, TEXT_HEIGHT))
  private val citizenIdReceiveButton = new JButton(CITIZEN_ID_BTN_RECEIVE)
  private val citizenIdStopButton = new JButton(CITIZEN_ID_BTN_STOP)

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

  /* categories panel */
  private val checkboxLayout = new JPanel
  checkboxLayout setLayout new FlowLayout()
  leafCategoriesCheck.foreach(checkboxLayout.add)
  mainPanel add checkboxLayout

  /* table panel */
  val tableModel = new DefaultTableModel()
  Seq("user", "id", "category", "feeder", "date", "value").foreach{tableModel.addColumn}
  val table = new JTable(tableModel)
  val tablePane = new JScrollPane(table)
  table setFillsViewportHeight true
  mainPanel add tablePane

  setSize(WIDTH, HEIGHT)
  setResizable(false)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setLocationRelativeTo(null)
  setTitle(title)
  setVisible(false)

  /* elements reaction logic */
  private var informationMap: Map[String, List[Data]] = Map()
  private var futureMap: Map[String, List[CancelableFuture[Unit]]] = Map()

  citizenIdReceiveButton addActionListener ( _ => {
    handleBtnClick (onlyNotEmptyLists = true, (user, categories) => {
      leafCategoriesCheck.foreach(_.setSelected(false))
      citizenIdField.setText("")
      val future = controller.subscribeTo(user, categories.toSet).foreach { data =>
        val newList = if (informationMap contains user) {
          data +: informationMap(user).filterNot(_.category.name == data.category.name)
        } else List(data)
        informationMap = informationMap + (user -> newList)
        refreshTable()
      }
      val futureList = List(future)
      futureMap =
        if(futureMap contains user)  futureMap + (user -> (futureMap(user) ++ futureList))
        else futureMap + (user -> futureList)
    })
  })

  citizenIdStopButton addActionListener ( _ => {
    handleBtnClick(onlyNotEmptyLists = false, (user, _) => {
      if(informationMap contains user) {
        leafCategoriesCheck.foreach(_.setSelected(false))
        citizenIdField.setText("")
        futureMap(user).foreach(_.cancel)
        futureMap = futureMap - user
        //controller unsubscribeFrom user
        refreshTable()
      } else {
        showDialog(this, NOT_SUBSCRIBED_ERROR)
      }
    })
  })

  private def handleBtnClick(onlyNotEmptyLists: Boolean, onSuccess: (String, Seq[LeafCategory]) => Unit): Unit = {
    val list = leafCategoriesCheck.filter(_.isSelected).map(_.leafCategory)
    val user = citizenIdField.getText
    if(user.hasWhiteSpaces) showDialog(this, EMPTY_TEXT_ERROR)
    else if(onlyNotEmptyLists && list.isEmpty) showDialog(this, SELECT_ONE_LEAF_ERROR)
    else onSuccess(user, list)
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
