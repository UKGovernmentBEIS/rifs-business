/*
 * Copyright (C) 2016  Department for Business, Energy and Industrial Strategy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rifs.business.tables

import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import play.api.libs.json.JsArray
import rifs.business.models._

class ApplicationFormTablesTest extends WordSpecLike with Matchers with OptionValues {

  "buildSections" should {
    "build a section with two questions" in {
      val afId = ApplicationFormId(1)
      val afsId = ApplicationFormSectionId(1)
      val afs = ApplicationFormSectionRow(afsId, afId, 1, "x", "form", JsArray())
      val q1 = ApplicationFormQuestionRow(ApplicationFormQuestionId(1), afsId, "x.a", "text 1", None, None)
      val q2 = ApplicationFormQuestionRow(ApplicationFormQuestionId(2), afsId, "x.a", "text 1", None, None)

      val input = Seq((afs, Some(q1)), (afs, Some(q2)))
      val output = ApplicationFormExtractors.buildSections(input)

      output.get(afsId).value.questions.length shouldBe 2
    }
  }

}
