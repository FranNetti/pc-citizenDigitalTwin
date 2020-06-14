package it.unibo.cop_medic.model.data

/**
 * a set of standard categories used in the covid domain.
 */
object Categories {
  //medical data
  val bodyTemperatureCategory = LeafCategory("bodyTemperature")
  val bloodOxygenCategory = LeafCategory("spo2")
  val heartrateCategory = LeafCategory("heartrate")
  val medicalRecordCategory  = LeafCategory("medicalRecord")
  val medicalDataCategory = GroupCategory("medicalData", Set(bodyTemperatureCategory, bloodOxygenCategory, medicalRecordCategory, heartrateCategory))
  //personal data
  val nameCategory = LeafCategory("name")
  val surnameCategory = LeafCategory("surname")
  val birthdateCategory = LeafCategory("birthdate")
  val fiscalCodeCategory = LeafCategory("cf")
  val personalDataCategory = GroupCategory("personal", Set(nameCategory, surnameCategory, birthdateCategory, fiscalCodeCategory))
  //position
  val positionCategory = LeafCategory("position")
  val locationCategory = GroupCategory("location", Set(positionCategory))


  def all: Seq[LeafCategory] = Seq(
    nameCategory, surnameCategory, birthdateCategory, fiscalCodeCategory,
    medicalRecordCategory, heartrateCategory, bodyTemperatureCategory, bloodOxygenCategory,
    positionCategory
  )

}
