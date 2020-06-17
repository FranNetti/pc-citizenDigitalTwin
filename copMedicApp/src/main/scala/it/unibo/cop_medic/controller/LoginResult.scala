package it.unibo.cop_medic.controller

import it.unibo.cop_medic.util.SystemUser

/**
 * Abstraction for the result of the login procedure.
 */
sealed trait LoginResult

case class SuccessfulLogin(userInfo: SystemUser) extends LoginResult
case class FailedLogin(cause: String) extends LoginResult
case object AlreadyLogged extends LoginResult
case object UnsupportedRole extends LoginResult