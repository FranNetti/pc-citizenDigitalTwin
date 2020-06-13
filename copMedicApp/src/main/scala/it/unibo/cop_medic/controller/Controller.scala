package it.unibo.cop_medic.controller

import java.net.URI
import java.util.Date
import java.util.concurrent.Executors

import io.vertx.core.json.JsonArray
import it.unibo.cop_medic.model.channel.{AuthenticationInfo, AuthenticationService, CitizenService, TokenIdentifier}
import it.unibo.cop_medic.model.channel.data.{Fail, Response}
import it.unibo.cop_medic.model.channel.parser.Parsers
import it.unibo.cop_medic.model.channel.rest.CitizenChannel
import it.unibo.cop_medic.model.data.{Data, LeafCategory, Resource, Roles}
import it.unibo.cop_medic.util.{AlreadyLogged, FailedLogin, LoginResult, SuccessfulLogin, SystemUser, UnsupportedRole}
import it.unibo.cop_medic.view.SubscriptionFailed
import it.unibo.cop_medic.view.View.ViewCreator
import monix.execution.{CancelableFuture, Scheduler}
import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.io.Source

sealed trait Controller {

  type DataValue = String
  type User = String

  def doLogin(username: String, password: String): Future[LoginResult]
  def subscribeTo(user: User, categories: Set[LeafCategory]): Observable[Data]
  def unsubscribeFrom(user: String)
  def addNewData(data: Seq[(DataValue, LeafCategory)])(user: User): Unit

}

object Controller {

  def defaultExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  def apply(viewCreator: ViewCreator, hostAuthentication: String, hostService: String, executionContext: ExecutionContext): Controller =
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

    override def addNewData(data: Seq[(DataValue, LeafCategory)])(user: User): Unit =
      if(logged) {
        val channel = this lookForCitizenChannel user
        val feeder = Resource(authenticationInfo.user.identifier)
        val timestamp = new Date().getTime
        channel updateState(
          authenticationInfo,
          data.map(x => Data(feeder = feeder, timestamp = timestamp, value = x._1, category = x._2, identifier = ""))
        ) whenComplete {
          case Response(content) =>
          case Fail(error) =>
        }
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

  }

}



