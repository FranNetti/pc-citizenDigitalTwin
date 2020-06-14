package it.unibo.cop_medic.controller

sealed trait InsertResult
case object SuccessfulInsert extends InsertResult
case class FailedInsert(error: String) extends InsertResult