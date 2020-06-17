package it.unibo.cop_medic.model.data

/**
 * A set of standard categories used in the COVID-19 domain.
 */
object Categories {
  //medical data
  val bodyTemperatureCategory: LeafCategory = LeafCategory("bodyTemperature")
  val bloodOxygenCategory: LeafCategory = LeafCategory("spo2")
  val heartrateCategory: LeafCategory = LeafCategory("heartrate")
  val medicalRecordCategory: LeafCategory  = LeafCategory("medicalRecord")
  val medicalDataCategory: GroupCategory = GroupCategory("medicalData", Set(bodyTemperatureCategory, bloodOxygenCategory, medicalRecordCategory, heartrateCategory))
  //personal data
  val nameCategory: LeafCategory = LeafCategory("name")
  val surnameCategory: LeafCategory = LeafCategory("surname")
  val birthdateCategory: LeafCategory = LeafCategory("birthdate")
  val fiscalCodeCategory: LeafCategory = LeafCategory("cf")
  val personalDataCategory: GroupCategory = GroupCategory("personal", Set(nameCategory, surnameCategory, birthdateCategory, fiscalCodeCategory))
  //position
  val positionCategory: LeafCategory = LeafCategory("position")
  val locationCategory: GroupCategory = GroupCategory("location", Set(positionCategory))

  /**
   * Get all categories.
   * @return a sequence containing all the categories
   */
  def all: Seq[LeafCategory] = Seq(
    nameCategory, surnameCategory, birthdateCategory, fiscalCodeCategory,
    medicalRecordCategory, heartrateCategory, bodyTemperatureCategory, bloodOxygenCategory,
    positionCategory
  )

}
