package rifs.business.tables

import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import play.api.libs.json.JsArray
import rifs.business.models._

class ApplicationFormTablesTest extends WordSpecLike with Matchers with OptionValues {

  "buildSections" should {
    "build a section with two questions" in {
      val afId = ApplicationFormId(1)
      val afsId = ApplicationFormSectionId(1)
      val afs = ApplicationFormSectionRow(afsId, afId, 1, "x", JsArray())
      val q1 = ApplicationFormQuestionRow(ApplicationFormQuestionId(1), afsId, "x.a", "text 1", None, None)
      val q2 = ApplicationFormQuestionRow(ApplicationFormQuestionId(2), afsId, "x.a", "text 1", None, None)

      val input = Seq((afs, Some(q1)), (afs, Some(q2)))
      val output = ApplicationFormExtractors.buildSections(input)

      output.get(afsId).value.questions.length shouldBe 2
    }
  }

}
