package it.unibo.cop_medic.controller

import java.net.URI

import it.unibo.cop_medic.model.category.LeafCategory
import it.unibo.cop_medic.model.channel.{AuthenticationInfo, AuthenticationService}
import it.unibo.cop_medic.model.channel.data.{Fail, Response}
import it.unibo.cop_medic.view.View
import it.unibo.cop_medic.view.View.ViewCreator

import scala.concurrent.ExecutionContext

sealed trait Controller {

  type DataValue = String
  type User = String

  def doLogin(username: String, password: String)
  def subscribeTo(user: User, categories: Set[LeafCategory])
  def unsubscribeFrom(user: String)
  def addNewData(data: Seq[(DataValue, LeafCategory)])(user: User)

}

object Controller {

  def apply(viewCreator: ViewCreator, hostAuthentication: String, hostService: String, executionContext: ExecutionContext): Controller =
    ControllerImpl(viewCreator, hostAuthentication, hostService, executionContext)

  private case class ControllerImpl(viewCreator: ViewCreator, hostAuthentication: String, hostService: String,
                                    implicit val executionContext: ExecutionContext) extends Controller {

    private val authenticationChannel = AuthenticationService createProxy URI.create(hostAuthentication)
    private val view = viewCreator(this)
    view.show()

    override def doLogin(username: String, password: String): Unit = {
      authenticationChannel.login(username, password).whenComplete {
        case Response(AuthenticationInfo(_, userInfo)) => view.loginSuccessful(userInfo)
        case Fail(error) => view loginFailed error.toString
      }
    }

    override def subscribeTo(user: User, categories: Set[LeafCategory]): Unit = ???

    override def unsubscribeFrom(user: String): Unit = ???

    override def addNewData(data: Seq[(DataValue, LeafCategory)])(user: User): Unit = ???
  }



}



