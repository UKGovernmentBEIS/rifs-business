package ifs.models

case class ParagraphId(id: Long) extends AnyVal

case class ParagraphRow(id: ParagraphId, paragraphNumber: Int, sectionId: SectionId, text: String)

case class SectionId(id: Long) extends AnyVal

case class SectionRow(id: SectionId, sectionNumber: Int, opportunityId: OpportunityId, title: String)

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