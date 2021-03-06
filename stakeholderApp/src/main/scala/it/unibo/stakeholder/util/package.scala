package it.unibo.stakeholder

package object util {

  /**
   * The representation of an user logged in the system.
   * It has:
   *  - an email
   *  - an username used to logged in
   *  - a secret password
   *  - a global identifier
   *  - a role
   */
  trait SystemUser {
    def email: String
    def username: String
    def password: String
    def identifier: String
    def role: String
  }

  object SystemUser {
    private case class SystemUserImpl(email: String, username: String, password: String, identifier: String, role: String) extends SystemUser
    def apply(email: String, username: String, password: String, identifier: String, role: String): SystemUser =
      SystemUserImpl(email, username, password, identifier, role)
  }

  implicit class RichString(value: String) {

    def hasNoWhiteSpaces: Boolean = !value.isEmpty && !value.trim.isEmpty

    def hasWhiteSpaces: Boolean = !hasNoWhiteSpaces

    def firstLetterUppercase: String = {
      val firstChar = value.charAt(0).toString
      value.replaceFirst(firstChar, firstChar.toUpperCase)
    }
  }

}
