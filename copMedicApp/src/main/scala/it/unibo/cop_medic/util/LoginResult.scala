package it.unibo.cop_medic.util

sealed trait LoginResult

case class SuccessfulLogin(userInfo: SystemUser) extends LoginResult
case class FailedLogin(cause: String) extends LoginResult
case object AlreadyLogged extends LoginResult
case object UnsupportedRole extends LoginResult