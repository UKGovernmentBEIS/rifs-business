package rifs.business.models

case class SectionId(id: Long) extends AnyVal

case class SectionRow(id: SectionId, sectionNumber: Int, opportunityId: OpportunityId, title: String, text: Option[String])

case class OpportunityId(id: Long) extends AnyVal

case class OpportunityRow(
                           id: OpportunityId,
                           title: String,
                           startDate: String,
                           duration: Option[Int],
                           durationUnits: Option[String],
                           value: BigDecimal,
                           valueUnits: String
                         )