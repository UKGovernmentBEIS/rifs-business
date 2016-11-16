package rifs.business

import com.wellfactored.playbindings.ValueClassFormats
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._
import rifs.business.models._
import rifs.business.restmodels._

import scala.util.Try

package object controllers extends ValueClassFormats {
  implicit val ldtfmt = new Format[LocalDateTime] {
    val dtf = DateTimeFormat.forPattern("dd MMM yyyy HH:mm:ss")

    override def writes(o: LocalDateTime): JsValue = JsString(dtf.print(o))

    override def reads(json: JsValue): JsResult[LocalDateTime] =
      implicitly[Reads[JsString]].reads(json).flatMap { js =>
        Try(dtf.parseLocalDateTime(js.value))
          .map(JsSuccess(_))
          .recover {
            case t: Throwable => JsError(t.getMessage)
          }.get
      }
  }
  implicit val paragraphFormat = Json.format[ParagraphRow]
  implicit val sectionFormat = Json.format[SectionRow]
  implicit val opportunityFormat = Json.format[OpportunityRow]
  implicit val questionFormat = Json.format[Question]

  implicit val oppDurFormat = Json.format[OpportunityDuration]
  implicit val oppValueFormat = Json.format[OpportunityValue]
  implicit val oppDescFormat = Json.format[OpportunityDescriptionSection]
  implicit val oppFormat = Json.format[Opportunity]

  implicit val appFormSecFormat = Json.format[ApplicationFormSection]
  implicit val appFormFormat = Json.format[ApplicationForm]

  implicit val appRowSecFormat = Json.format[ApplicationSectionRow]
  implicit val appRowFormat = Json.format[ApplicationRow]
  implicit val appSecFormat = Json.format[ApplicationSection]
  implicit val appFormat = Json.format[Application]
}
