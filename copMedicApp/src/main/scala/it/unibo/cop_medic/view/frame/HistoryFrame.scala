package it.unibo.cop_medic.view.frame

import java.awt.Dimension
import java.awt.event.{WindowAdapter, WindowEvent}

import it.unibo.cop_medic.controller.Controller
import it.unibo.cop_medic.model.data.{Data, DataCategory}
import it.unibo.cop_medic.view.{View, ViewController}
import it.unibo.cop_medic.view.frame.panel.HistoryHeaderPanel
import javax.swing.{BoxLayout, JFrame, JPanel, JScrollPane}
import javax.swing.border.EmptyBorder
import monix.execution.Scheduler

import scala.util.{Failure, Success}

object HistoryFrame {

  def apply(title: String, dataCategory: DataCategory, controller: Controller, viewController: ViewController): HistoryFrame =
    new HistoryFrame(title, dataCategory, controller, viewController)

  private val WIDTH = 900
  private val HEIGHT = 480
  private val CITIZEN_FIELD_LENGTH = 400

  private val HISTORY_LENGTH = 100

  private val CITIZEN_ID_LABEL = "Citizen id: "
  private val CITIZEN_ID_HINT = "Insert id..."
  private val HISTORY_BUTTON = "GET HISTORY"
  private val EMPTY_TEXT_ERROR = "The user id must not be empty!"
  private val ERROR = "An error occurred during the operation: "

}

/**
 * View for the history.
 * @param title the view title
 * @param dataCategory the category you want the history
 * @param controller the main controller of the application
 * @param viewController the view controller to use
 */
private[view] class HistoryFrame(title: String, dataCategory: DataCategory, controller: Controller, viewController: ViewController) extends JFrame {

  import HistoryFrame._

  private implicit val scheduler = Scheduler(View.defaultExecutionContext)

  /* Main panel */
  private val mainPanel = new JPanel
  mainPanel setBorder new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN)
  mainPanel setLayout new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)
  getContentPane add mainPanel

  private val historyHeader = HistoryHeaderPanel(
    CITIZEN_ID_LABEL, CITIZEN_ID_HINT, new Dimension(CITIZEN_FIELD_LENGTH, TEXT_HEIGHT), HISTORY_BUTTON
  )
  mainPanel add historyHeader
  mainPanel add createVerticalBox()

  /* table panel */
  private val (table,tableModel) = createTable(Seq("id", "category", "feeder", "date", "value"))
  private val tablePane = new JScrollPane(table)
  mainPanel add tablePane

  setSize(WIDTH, HEIGHT)
  setResizable(false)
  setLocationRelativeTo(null)
  setTitle(title)
  setVisible(false)
  this addWindowListener new WindowAdapter() {
    override def windowClosing(ev: WindowEvent): Unit = {
      viewController.showApplicationView
    }
  }

  /* elements reaction logic */
  private var historyData: Seq[Data] = Seq()

  historyHeader.handleGetHistoryButton {user =>
    controller.requestHistory(user, dataCategory)(HISTORY_LENGTH).onComplete {
      case Success(history) =>
        historyData = history
        refreshTable()
      case Failure(exception) => showDialog(this, ERROR + exception.getLocalizedMessage)
    }
  }(EMPTY_TEXT_ERROR)

  private def refreshTable(): Unit = {
    val newList = historyData.map(toTableFormat)
    for(i <- 0 until tableModel.getRowCount) {
      tableModel.removeRow(0)
    }
    newList.foreach(tableModel.addRow)
  }

}
