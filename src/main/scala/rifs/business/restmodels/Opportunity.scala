package rifs.business.restmodels

import org.joda.time.DateTime
import rifs.business.models.OpportunityId

case class OpportunityDescriptionSection(sectionNumber: Int, title: String, text: Option[String])

case class OpportunityValue(amount: BigDecimal, unit: String)

case class OpportunityDuration(duration: Int, units: String)

case class Opportunity(
                        id: OpportunityId,
                        title: String,
                        startDate: String,
                        endDate: Option[String],
                        value: OpportunityValue,
                        publishedAt : Option[DateTime],
                        duplicatedFrom:Option[OpportunityId],
                        description: Set[OpportunityDescriptionSection]

                      ) {
  def summary: OpportunitySummary = OpportunitySummary(id, title, startDate, endDate, value, publishedAt, duplicatedFrom)
}

case class OpportunitySummary(
                               id: OpportunityId,
                               title: String,
                               startDate: String,
                               endDate: Option[String],
                               value: OpportunityValue,
                               publishedAt : Option[DateTime],
                               duplicatedFrom:Option[OpportunityId]
                             )
