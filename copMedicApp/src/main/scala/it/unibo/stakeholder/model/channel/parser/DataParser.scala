package it.unibo.stakeholder.model.channel.parser

import it.unibo.stakeholder.model.channel.parser.ParserLike.MismatchableParser
import it.unibo.stakeholder.model.data.{Data, LeafCategory}

/**
 * This concept allow to marshall and marshalling information expressed in some Raw codification.
 * It has the responsibility to decode/encode a set of data category
 *
 * @tparam External the type of codification used to store data externally (e.g. Json, Xml,String,..)
 */
trait DataParser[External] extends MismatchableParser[External, Data] {
  /**
   * @return all categories supported by this data parser
   */
  def supportedCategories : Seq[LeafCategory]
}
