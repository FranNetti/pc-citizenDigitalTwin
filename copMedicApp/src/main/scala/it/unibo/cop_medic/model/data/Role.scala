package it.unibo.cop_medic.model.data

import it.unibo.cop_medic.model.data.Categories._

case class Role(name: String)

object Roles {

  object CopRole extends Role("policeman")
  object MedicRole extends Role("medic")
  object CitizenRole extends Role("citizen")

  def categoriesByRole(role: Role): Seq[DataCategory] = role match {
    case CopRole => personalDataCategory.dataCategory.toSeq ++ locationCategory.dataCategory.toSeq
    case MedicRole => personalDataCategory.dataCategory.toSeq ++ medicalDataCategory.dataCategory.toSeq
    case CitizenRole => Categories.all
    case _ => Seq()
  }

  def all: Seq[Role] = Seq(CopRole, MedicRole, CitizenRole)


}
