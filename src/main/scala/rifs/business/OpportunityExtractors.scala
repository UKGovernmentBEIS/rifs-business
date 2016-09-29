package rifs.business

import rifs.business.restmodels.{Opportunity, OpportunityDescriptionSection, OpportunityDuration, OpportunityValue}
import rifs.models.{OpportunityRow, ParagraphRow, SectionRow}

object OpportunityExtractors {
  /**
    * Accepts the results of the left joins across the Opportunity, Section and Paragraph tables and
    * builds them up into the REST model.
    *
    * @param joinResults the raw results from the left joins.
    * @return
    */
  def extractOpportunities(joinResults: Seq[(OpportunityRow, Option[(SectionRow, Option[ParagraphRow])])]): Seq[Opportunity] = {
    val (opps: Seq[OpportunityRow], y: Seq[Option[(SectionRow, Option[ParagraphRow])]]) = joinResults.unzip
    val (sections, oparas) = y.flatten.unzip
    val paras: Seq[ParagraphRow] = oparas.flatten

    val paraMap = paras.groupBy(_.sectionId)
    val sectionMap = sections.distinct.groupBy(_.opportunityId)
    opps.distinct.map { opp =>
      val sections: Seq[OpportunityDescriptionSection] = sectionMap.get(opp.id).map { ss =>
        ss.flatMap { s =>
          paraMap.get(s.id).map { ps =>
            OpportunityDescriptionSection(s.sectionNumber, s.title, ps.map(_.text))
          }
        }
      }.getOrElse(Seq())

      val du = for {
        d <- opp.duration
        u <- opp.durationUnits
      } yield OpportunityDuration(d, u)

      Opportunity(opp.id, opp.title, opp.startDate, du, OpportunityValue(opp.value, opp.valueUnits), sections)
    }
  }
}
