package it.unibo.cop_medic.model.data

object History {
  /**
   * type alisas for historical data (a Seq of Data)
   */
  type History = Seq[Data]
  def apply(data: Seq[Data]): History = data
}
