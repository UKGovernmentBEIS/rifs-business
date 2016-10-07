package rifs.business

import org.scalatest._
import rifs.business.restmodels.OpportunityDuration
import rifs.business.tables.OpportunityExtractors
import rifs.models._

class OpportunityExtractors$Test extends WordSpecLike with Matchers with OptionValues {

  "durationFor" should {
    import OpportunityExtractors.durationFor

    "return None if duration is None" in {
      val opp = OpportunityRow(OpportunityId(1), "", "", None, None, 0.0, "")
      durationFor(opp) shouldBe None
    }

    "return None if units is None" in {
      val opp = OpportunityRow(OpportunityId(1), "", "", Some(1), None, 0.0, "")
      durationFor(opp) shouldBe None
    }

    "return Some if both duration and units are defined" in {
      val opp = OpportunityRow(OpportunityId(1), "", "", Some(1), Some("x"), 0.0, "")
      durationFor(opp).value shouldBe OpportunityDuration(1, "x")
    }
  }

  "extractOpportunities" should {
    "extract the right Opportunity objects" in {
      val result = OpportunityExtractors.extractOpportunities(testData)

      result.size shouldBe 1
      result.head.description.size shouldBe 6
      val section1 = result.head.description.find(_.sectionNumber == 1).value
      section1.paragraphs.length shouldBe 5
    }
  }


  lazy val testData = Seq(
    (OpportunityRow(OpportunityId(1), "Research priorities in health care", "4 March 2017", None, None, 2000.00, "per event maximum"),
      Some((SectionRow(SectionId(6), 6, OpportunityId(1), "Further Information"), None))),
    (OpportunityRow(OpportunityId(1), "Research priorities in health care", "4 March 2017", None, None, 2000.00, "per event maximum"),
      Some((SectionRow(SectionId(5), 5, OpportunityId(1), "Assessment Criteria"), None))),
    (OpportunityRow(OpportunityId(1), "Research priorities in health care", "4 March 2017", None, None, 2000.00, "per event maximum"),
      Some((SectionRow(SectionId(4), 4, OpportunityId(1), "How to get funding"), None))),
    (OpportunityRow(OpportunityId(1), "Research priorities in health care", "4 March 2017", None, None, 2000.00, "per event maximum"),
      Some((SectionRow(SectionId(3), 3, OpportunityId(1), "What events hould cover"), None))),
    (OpportunityRow(OpportunityId(1), "Research priorities in health care", "4 March 2017", None, None, 2000.00, "per event maximum"),
      Some((SectionRow(SectionId(2), 2, OpportunityId(1), "The events we will fund"),
        Some(ParagraphRow(ParagraphId(8), 3, SectionId(2), "We advise that attendees are invited from relevant faculties, colleges or departments, and where the primary aim is knowledge exchange, relevant stakeholders should be invited e.g.representatives from industry."))))),
    (OpportunityRow(OpportunityId(1), "Research priorities in health care", "4 March 2017", None, None, 2000.00, "per event maximum"),
      Some((SectionRow(SectionId(2), 2, OpportunityId(1), "The events we will fund"),
        Some(ParagraphRow(ParagraphId(7), 2, SectionId(2), "We encourage applications that are coordinated across departments within a research organisation or between different research organisations."))))),
    (OpportunityRow(OpportunityId(1), "Research priorities in health care", "4 March 2017", None, None, 2000.00, "per event maximum"),
      Some((SectionRow(SectionId(2), 2, OpportunityId(1), "The events we will fund"),
        Some(ParagraphRow(ParagraphId(6), 1, SectionId(2), "To receive funding for the event, your research organisation must receive funding from the research council and must aim to attract research council supported researchers to the event."))))),
    (OpportunityRow(OpportunityId(1), "Research priorities in health care", "4 March 2017", None, None, 2000.00, "per event maximum"),
      Some((SectionRow(SectionId(1), 1, OpportunityId(1), "About this opportunity"),
        Some(ParagraphRow(ParagraphId(5), 5, SectionId(1), "Only organisations which receive funding from UK Research Councils may apply."))))),
    (OpportunityRow(OpportunityId(1), "Research priorities in health care", "4 March 2017", None, None, 2000.00, "per event maximum"),
      Some((SectionRow(SectionId(1), 1, OpportunityId(1), "About this opportunity"),
        Some(ParagraphRow(ParagraphId(4), 4, SectionId(1), "Under the Exploring Innovation Seminars programme, we will pay up to 2,000 for each event promoting innovation and collaboration.We will not pay for food or drink."))))),
    (OpportunityRow(OpportunityId(1), "Research priorities in health care", "4 March 2017", None, None, 2000.00, "per event maximum"),
      Some((SectionRow(SectionId(1), 1, OpportunityId(1), "About this opportunity"),
        Some(ParagraphRow(ParagraphId(3), 3, SectionId(1), "This may be by sharing knowledge, commercialising ideas, exploring social benefits or other ways to increase the impact of your research."))))),
    (OpportunityRow(OpportunityId(1), "Research priorities in health care", "4 March 2017", None, None, 2000.00, "per event maximum"),
      Some((SectionRow(SectionId(1), 1, OpportunityId(1), "About this opportunity"),
        Some(ParagraphRow(ParagraphId(2), 2, SectionId(1), "As part of this, we want to help you to develop innovative ways of building on the research they carry out."))))),
    (OpportunityRow(OpportunityId(1), "Research priorities in health care", "4 March 2017", None, None, 2000.00, "per event maximum"),
      Some((SectionRow(SectionId(1), 1, OpportunityId(1), "About this opportunity"),
        Some(ParagraphRow(ParagraphId(1), 1, SectionId(1), "We want to achieve the widest benefit to society and the economy from the research we fund."))))))

}
