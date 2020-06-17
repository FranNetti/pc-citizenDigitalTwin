package it.unibo.cop_medic.controller

import java.net.URI
import java.util.Date
import java.util.concurrent.Executors

import io.vertx.core.json.JsonArray
import it.unibo.cop_medic.model.channel.{AuthenticationInfo, AuthenticationService, CitizenService, TokenIdentifier}
import it.unibo.cop_medic.model.channel.data.{Fail, Response}
import it.unibo.cop_medic.model.channel.parser.Parsers
import it.unibo.cop_medic.model.channel.rest.CitizenChannel
import it.unibo.cop_medic.model.data.{Categories, Data, LeafCategory, Resource, Roles}
import it.unibo.cop_medic.util.SystemUser
import it.unibo.cop_medic.view.SubscriptionFailed
import it.unibo.cop_medic.view.View.ViewCreator
import monix.execution.{CancelableFuture, Scheduler}
import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._
import scala.io.Source

/**
 * Trait for main controllers of the application.
 */
sealed trait Controller {

  type DataValue = String
  type User = String

  /**
   * Do the login procedure.
   * @param username the user username
   * @param password the user password
   * @return a future with the result of the operation
   */
  def doLogin(username: String, password: String): Future[LoginResult]

  /**
   * Subscribe to a user in order to receive updates for each new data of a given category.
   * @param user the user you want to subscribe to
   * @param categories the category you're interested in
   * @return an observable where the new data received will be published
   */
  def subscribeTo(user: User, categories: Set[LeafCategory]): Observable[Data]

  /**
   * Unsubscribe from a user in order to not receive any more notification of a new data.
   * @param user the user you want to unsubscribe from
   */
  def unsubscribeFrom(user: String)

  /**
   * Publish a sequence of data for the given user.
   * @param data a sequence of (information, category) that corresponds to multiple data
   * @param user the user you want to update
   * @return a future with the result of the operation
   */
  def addNewData(data: Seq[(Seq[DataValue], LeafCategory)])(user: User): Future[InsertResult]

}

object Controller {

  def defaultExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  /**
   * Create a new Controller.
   * @param viewCreator the view creation strategy
   * @param hostAuthentication the host of the authentication service
   * @param hostService the host of the citizen service
   * @param executionContext the execution context of the main application
   * @return a new instance of Controller
   */
  def apply(viewCreator: ViewCreator, hostAuthentication: String, hostService: String,
            executionContext: ExecutionContext = Controller.defaultExecutionContext): Controller =
    ControllerImpl(viewCreator, hostAuthentication, hostService, executionContext)

  private implicit def fromAuthenticationInfoToTokenIdentifier(authenticationInfo: AuthenticationInfo): TokenIdentifier =
    TokenIdentifier(authenticationInfo.token.token)

  private case class ControllerImpl(viewCreator: ViewCreator, hostAuthentication: String, hostService: String,
                                    implicit val executionContext: ExecutionContext) extends Controller {

    private val monixContext = Scheduler(executionContext)
    private val registry = Parsers.configureRegistryFromJson(new JsonArray(Source.fromResource("categories.json").mkString))
    private val authenticationChannel = AuthenticationService createProxy URI.create(s"http://${hostAuthentication}:8081")
    private val view = viewCreator(this)

    private var logged = false
    private var authenticationInfo: AuthenticationInfo = _
    private var citizenChannels: Map[User, CitizenChannel] = Map()
    private var futureMap: Map[User, List[CancelableFuture[Unit]]] = Map()
    view.show()

    override def doLogin(username: String, password: String): Future[LoginResult] = {
      val promise = Promise[LoginResult]()
      if(!logged) {
        authenticationChannel.login(username, password)
          .whenComplete {
              case Response(auth: AuthenticationInfo) if isRoleSupported(auth.user) =>
                logged = true
                authenticationInfo = auth
                this backgroundTokenRefresh()
                promise success SuccessfulLogin(auth.user)
              case Response(_) => promise success UnsupportedRole
              case Fail(error) => promise success FailedLogin(error.toString)
          }
      } else {
        promise success AlreadyLogged
      }
      promise.future
    }

    override def subscribeTo(user: User, categories: Set[LeafCategory]): Observable[Data] = {
      val observable = PublishSubject[Data]()
      if(logged) {
        val channel = this lookForCitizenChannel user
        categories foreach { x =>
          channel.observeState(authenticationInfo, x) whenComplete {
            case Response(content) =>
              val futureList = List(content.foreach(observable.onNext)(monixContext))
              futureMap =
                if(futureMap contains user)  futureMap + (user -> (futureMap(user) ++ futureList))
                else futureMap + (user -> futureList)
            case Fail(error) => view showError SubscriptionFailed(error.toString)
          }
        }
        channel.readState(authenticationInfo) whenComplete {
          case Response(content) => content.filter(x => categories.contains(x.category)).foreach(observable.onNext)
          case Fail(error) => System.err.println("Error in subscribeTo: " + error)
        }
      }
      observable
    }

    override def unsubscribeFrom(user: String): Unit = {
      futureMap(user).foreach(_.cancel)
      futureMap = futureMap - user
      citizenChannels = citizenChannels - user
    }

    override def addNewData(data: Seq[(Seq[DataValue], LeafCategory)])(user: User): Future[InsertResult] = {
      val promise = Promise[InsertResult]()
      if (logged) {
        val channel = this lookForCitizenChannel user
        val feeder = Resource(authenticationInfo.user.identifier)
        val timestamp = new Date().getTime
        try {
          val formattedData: Seq[Data] = data.map(x =>
            Data(feeder = feeder, timestamp = timestamp, value = formatData(x._2, x._1), category = x._2, identifier = "")
          )
          channel updateState(authenticationInfo, formattedData) whenComplete {
            case Response(_) => promise success SuccessfulInsert
            case Fail(error) => promise success FailedInsert(error.toString)
          }
        } catch {
            case e: Exception => promise failure e
        }
      } else {
        promise success FailedInsert("You're not logged")
      }
      promise.future
    }

    private def isRoleSupported(user: SystemUser): Boolean = user.role match {
      case Roles.CopRole.name | Roles.MedicRole.name => true
      case _ => false
    }

    private def lookForCitizenChannel(user: User): CitizenChannel = {
      if (citizenChannels contains user) citizenChannels(user)
      else {
        val newChannel = CitizenService createProxy(user, registry, hostService)
        citizenChannels = citizenChannels + (user -> newChannel)
        newChannel
      }
    }

    private def formatData(leafCategory: LeafCategory, values: Seq[String]): Any = leafCategory match {
      case Categories.bodyTemperatureCategory => (values.head.toDouble, "°C")
      case Categories.bloodOxygenCategory | Categories.heartrateCategory => values.head.toDouble
      case Categories.positionCategory => (values.head.toDouble, values(1).toDouble)
      case Categories.medicalRecordCategory => values
      case _ => values.head
    }

    private def backgroundTokenRefresh(): Unit = {
      new Thread(() => {
        while(true) {
          val expirationTime = authenticationInfo.token.expirationInMinute.minutes
          val timeToWait = expirationTime - 1.minute
          Thread.sleep(timeToWait.toMillis)
          authenticationChannel refresh authenticationInfo whenComplete {
            case Response(newToken) => authenticationInfo = AuthenticationInfo(newToken, authenticationInfo.user)
          }
        }
      }).start()
    }

  }

}



