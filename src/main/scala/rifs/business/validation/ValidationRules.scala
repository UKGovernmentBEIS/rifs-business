package rifs.business.validation

object ValidationRules {

  val rule1 = "startDate is mandatory"
  val rule2 = "date(startDate.day, startDate.month, startDate.year) is not before today"
  val rule3 = "title is no longer than 20 words"
  val rule4 = "provisionalDate.days is between 1 and 9"

}
