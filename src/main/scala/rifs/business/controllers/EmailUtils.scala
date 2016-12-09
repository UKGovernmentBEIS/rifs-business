package rifs.business.controllers

import play.api.Configuration


trait EmailUtils {
  final val RIFS_EMAIL = "rifs.email"
  final val RIFS_DUMMY_APPLICANT_EMAIL = s"$RIFS_EMAIL.dummyapplicant"
  final val RIFS_REPLY_TO_EMAIL = s"$RIFS_EMAIL.replyto"
  final val RIFS_DUMMY_MANAGER_EMAIL = s"$RIFS_EMAIL.dummymanager"

  def managerEmail(config: Configuration ) = config.underlying.getString(RIFS_DUMMY_MANAGER_EMAIL)
  def fromAddress(config: Configuration ) = config.underlying.getString(RIFS_REPLY_TO_EMAIL)
}
