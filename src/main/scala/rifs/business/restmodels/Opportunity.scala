package rifs.business.restmodels

import rifs.business.models.OpportunityId

case class OpportunityDescriptionSection(sectionNumber: Int, title: String, paragraphs: Seq[String])

case class OpportunityValue(amount: BigDecimal, unit: String)

case class OpportunityDuration(duration: Int, units: String)

case class Opportunity(
                        id: OpportunityId,
                        title: String,
                        startDate: String,
                        duration: Option[OpportunityDuration],
                        value: OpportunityValue,
                        description: Set[OpportunityDescriptionSection]
                      ) {
  def summary: OpportunitySummary = OpportunitySummary(id, title, startDate, duration, value)
}

case class OpportunitySummary(
                               id: OpportunityId,
                               title: String,
                               startDate: String,
                               duration: Option[OpportunityDuration],
                               value: OpportunityValue
                             )
