package rifs.business.restmodels

import rifs.business.models.OpportunityId

case class OpportunitySection(sectionNumber: Int, title: String, text: Option[String])

case class OpportunityValue(amount: BigDecimal, unit: String)

case class OpportunityDuration(duration: Int, units: String)

case class Opportunity(
                        id: OpportunityId,
                        title: String,
                        startDate: String,
                        duration: Option[OpportunityDuration],
                        value: OpportunityValue,
                        sections: Set[OpportunitySection]
                      )
