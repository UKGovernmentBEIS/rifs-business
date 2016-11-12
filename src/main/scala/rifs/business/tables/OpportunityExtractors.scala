package rifs.business.tables

import cats.instances.option._
import cats.syntax.cartesian._
import rifs.business.models.{OpportunityRow, SectionRow}
import rifs.business.restmodels.{Opportunity, OpportunityDuration, OpportunitySection, OpportunityValue}

object OpportunityExtractors {
  /**
    * Accepts the results of the left joins across the Opportunity, Section and Paragraph tables and
    * builds them up into the REST model.
    *
    * @param joinResults the raw results from the left joins.
    */
  def extractOpportunities(joinResults: Seq[(OpportunityRow, Option[SectionRow])]): Set[Opportunity] = {
    val (oppRows, sectionRows) = splitOutRowSets(joinResults)

    oppRows.map { o =>
      Opportunity(
        o.id,
        o.title,
        o.startDate,
        durationFor(o),
        OpportunityValue(o.value, o.valueUnits),
        sectionsFor(o, sectionRows))
    }
  }

  def sectionsFor(o: OpportunityRow, ss: Set[SectionRow]): Set[OpportunitySection] = {
    ss.filter(_.opportunityId == o.id).map(s => buildSection(s))
  }

  def buildSection(s: SectionRow): OpportunitySection = OpportunitySection(s.sectionNumber, s.title, s.text)

  def durationFor(opp: OpportunityRow): Option[OpportunityDuration] = {
    (opp.duration |@| opp.durationUnits).map(OpportunityDuration.apply)
  }

  /**
    * Take the nested results from the left joins and separate out sequences of the two `Row` types
    *
    * @return a pair of `Set`s of each of the row types. Because the left join can result in sql `null`s
    *         (represented as Scala `None`s), the two sequences will generally be of different lengths from
    *         each other.
    */
  def splitOutRowSets(joinResults: Seq[(OpportunityRow, Option[SectionRow])]): (Set[OpportunityRow], Set[SectionRow]) = {
    val sections = joinResults.flatMap(_._2)
    val opps = joinResults.map(_._1)

    (opps.toSet, sections.toSet)
  }

  /**
    * useful for extracting from leftJoin results where the second item in the pair is represented as
    * an Option
    */
  def unzipO[A, B](join: Seq[(A, Option[B])]): (Seq[A], Seq[B]) = join.unzip match {
    case (as, bs) => (as, bs.flatten)
  }
}
