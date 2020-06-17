package it.unibo.cop_medic.model.data

/**
 * This concept is used to create an "high" level taxonomy associated to the data category:
 */
sealed trait DataCategory {
  def name : String
}

/**
 * Group a set of linked category.
 * @param name : the name
 * @param dataCategory : a set of category related to this one
 */
case class GroupCategory(name : String, dataCategory: Set[DataCategory]) extends DataCategory

/**
 * A leaf category, described an "atomic" information depending on domain
 * @param name : the name
 * @param TTL: a temporal information to the lifespan, default -1 -> unlimited.
 */
class LeafCategory(val name : String, val TTL : Long = -1) extends DataCategory {
  def isLimited : Boolean = TTL > 0

  def canEqual(other: Any): Boolean = other.isInstanceOf[LeafCategory]

  override def equals(other: Any): Boolean = other match {
    case that: LeafCategory =>
      (that canEqual this) &&
        name == that.name
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(name)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = s"LeafCategory($name)"
}

object LeafCategory {
  def apply(name : String, TTL : Long = -1) : LeafCategory = new LeafCategory(name, TTL)
  def unapply(arg: LeafCategory): Option[(String, Long)] = Some((arg.name, arg.TTL))
}

object DataCategoryOps {
  /**
   * Extract all child categories from a DataCategory. If the DataCategories is Leaf, returns itself.
   * @param dataCategory the data category to expand
   * @return all child starting form data category
   */
  def allChild(dataCategory: DataCategory) : Set[LeafCategory] = dataCategory match {
    case value : LeafCategory => Set(value)
    case GroupCategory(_, categories) => categories.flatMap(allChild)
  }
}
