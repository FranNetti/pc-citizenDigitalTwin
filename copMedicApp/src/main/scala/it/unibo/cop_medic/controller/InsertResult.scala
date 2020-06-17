package it.unibo.cop_medic.controller

/**
 * Abstraction for the result of the sending of a new data to the back-end.
 */
sealed trait InsertResult

case object SuccessfulInsert extends InsertResult
case class FailedInsert(error: String) extends InsertResult