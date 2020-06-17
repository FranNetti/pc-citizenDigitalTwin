package it.unibo.cop_medic.model.data

/**
 * It describes a feeder of information.
 */
sealed trait Feeder

/**
 * A feeder can be a resource (according to the REST semantics).
 * @param URI: ID associated to the resource that produce the data
 */
case class Resource(URI : String) extends Feeder

/**
 * A feeder can be a generic sensor that produce some information.
 * @param name the sensor name
 */
case class Sensor(name : String) extends Feeder
