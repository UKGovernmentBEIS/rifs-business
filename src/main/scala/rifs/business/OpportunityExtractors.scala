package rifs.business

import rifs.business.restmodels.{Opportunity, OpportunityDescriptionSection, OpportunityDuration, OpportunityValue}
import rifs.models._

object OpportunityExtractors {
  /**
    * Accepts the results of the left joins across the Opportunity, Section and Paragraph tables and
    * builds them up into the REST model.
    *
    * @param joinResults the raw results from the left joins.
    */
  def extractOpportunities(joinResults: Seq[(OpportunityRow, Option[(SectionRow, Option[ParagraphRow])])]): Seq[Opportunity] = {
    val (os, ss, ps) = splitOutRows(joinResults)

    val sectionMap = ss.map { s =>
      s.opportunityId -> OpportunityDescriptionSection(s.sectionNumber, s.title, ps.filter(_.sectionId == s.id).map(_.text))
    }.groupBy(_._1)

    os.map { o =>
      Opportunity(o.id, o.title, o.startDate, durationFor(o), OpportunityValue(o.value, o.valueUnits),
        sectionMap.getOrElse(o.id, Seq()).map(_._2))
    }
  }

  def durationFor(opp: OpportunityRow): Option[OpportunityDuration] = for {
    d <- opp.duration
    u <- opp.durationUnits
  } yield OpportunityDuration(d, u)

  /**
    * Take the nested results from the left joins and separate out sequences of the three `Row` types
    *
    * @return a triple of `Seq`s of each of the row types. Because the left joins can result in sql `null`s
    *         (represented as Scala `None`s), the three sequences will generally be of different lengths from
    *         each other.
    */
  def splitOutRows(joinResults: Seq[(OpportunityRow, Option[(SectionRow, Option[ParagraphRow])])]): (Seq[OpportunityRow], Seq[SectionRow], Seq[ParagraphRow]) = {
    val (os, ss, ps) = joinResults.map {
      case (o, Some((s, Some(p)))) => (o, Some(s), Some(p))
      case (o, Some((s, None))) => (o, Some(s), None)
      case (o, None) => (o, None, None)
    }.unzip3

    (os.distinct, ss.flatten.distinct, ps.flatten.distinct)
  }
}
