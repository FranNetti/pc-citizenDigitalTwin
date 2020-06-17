package it.unibo.cop_medic.model.channel.coap

import org.eclipse.californium.core.coap.MediaTypeRegistry

/**
 * Indicates the type of the content exchanged between back-end and front-end.
 */
sealed trait ContentFormat {
  def code : Int
}

protected class BaseContentFormat(override val code : Int) extends ContentFormat
case object PlainFormat extends BaseContentFormat(MediaTypeRegistry.TEXT_PLAIN)
case object JsonFormat extends BaseContentFormat(MediaTypeRegistry.APPLICATION_JSON)
