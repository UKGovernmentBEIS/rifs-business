package rifs.business.tables

import rifs.business.restmodels.{Opportunity, OpportunityDescriptionSection, OpportunityDuration, OpportunityValue}
import rifs.models._

object OpportunityExtractors {
  /**
    * Accepts the results of the left joins across the Opportunity, Section and Paragraph tables and
    * builds them up into the REST model.
    *
    * @param joinResults the raw results from the left joins.
    */
  def extractOpportunities(joinResults: Seq[(OpportunityRow, Option[(SectionRow, Option[ParagraphRow])])]): Set[Opportunity] = {
    val (oppRows, sectionRows, paraRows) = splitOutRowSets(joinResults)

    oppRows.map { o =>
      Opportunity(
        o.id,
        o.title,
        o.startDate,
        durationFor(o),
        OpportunityValue(o.value, o.valueUnits),
        sectionsFor(o, sectionRows, paraRows))
    }
  }

  def sectionsFor(o: OpportunityRow, ss: Set[SectionRow], ps: Set[ParagraphRow]): Set[OpportunityDescriptionSection] = {
    ss.filter(_.opportunityId == o.id).map(s => buildSection(s, ps))
  }

  def buildSection(s: SectionRow, ps: Set[ParagraphRow]): OpportunityDescriptionSection = {
    OpportunityDescriptionSection(s.sectionNumber, s.title, ps.filter(_.sectionId == s.id).toSeq.sortBy(_.paragraphNumber).map(_.text))
  }

  def durationFor(opp: OpportunityRow): Option[OpportunityDuration] = for {
    d <- opp.duration
    u <- opp.durationUnits
  } yield OpportunityDuration(d, u)

  /**
    * Take the nested results from the left joins and separate out sequences of the three `Row` types
    *
    * @return a triple of `Set`s of each of the row types. Because the left joins can result in sql `null`s
    *         (represented as Scala `None`s), the three sequences will generally be of different lengths from
    *         each other.
    */
  def splitOutRowSets(joinResults: Seq[(OpportunityRow, Option[(SectionRow, Option[ParagraphRow])])]): (Set[OpportunityRow], Set[SectionRow], Set[ParagraphRow]) = {
    val (sections, paragraphs) = unzipO(joinResults.flatMap(_._2))
    val opps = joinResults.map(_._1)

    (opps.toSet, sections.toSet, paragraphs.toSet)
  }

  /**
    * useful for extracting from leftJoin results where the second item in the pair is represented as
    * an Option
    */
  def unzipO[A, B](join: Seq[(A, Option[B])]): (Seq[A], Seq[B]) = join.unzip match {
    case (as, bs) => (as, bs.flatten)
  }
}
