package it.unibo.stakeholder.model.data

import it.unibo.stakeholder.model.data.Categories._

/**
 * Trait that represents a CDT role supported by this application.
 */
sealed trait Role {
  def name: String
}

object Roles {

  object CopRole extends Role { val name = "policeman" }
  object MedicRole extends Role { val name = "medic" }
  object CitizenRole extends Role { val name = "citizen" }

  /**
   * Get a sequence of data category based on the role.
   * @param role the role
   * @return a sequence of data category
   */
  def categoriesByRole(role: Role): Seq[DataCategory] = role match {
    case CopRole => personalDataCategory.dataCategory.toSeq ++ locationCategory.dataCategory.toSeq
    case MedicRole => personalDataCategory.dataCategory.toSeq ++ medicalDataCategory.dataCategory.toSeq
    case CitizenRole => Categories.all
    case _ => Seq()
  }

  /**
   * Get all roles.
   * @return a sequence containing all the roles
   */
  def all: Seq[Role] = Seq(CopRole, MedicRole, CitizenRole)


}
