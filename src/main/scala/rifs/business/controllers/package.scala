package rifs.business

import com.wellfactored.playbindings.ValueClassFormats
import ifs.data.restmodels.{Opportunity, OpportunityDescriptionSection, OpportunityDuration, OpportunityValue}
import ifs.models._
import org.joda.time.{LocalDate, LocalDateTime}
import play.api.libs.json._

package object controllers extends ValueClassFormats {
  implicit val jodaLocalDateTimeFormat = new Format[LocalDateTime] {
    override def writes(o: LocalDateTime): JsValue =
      JsArray(
        Seq(
          JsNumber(o.getYear),
          JsNumber(o.getMonthOfYear),
          JsNumber(o.getDayOfMonth),
          JsNumber(o.getHourOfDay),
          JsNumber(o.getMinuteOfHour),
          JsNumber(o.getSecondOfMinute)
        ))

    override def reads(json: JsValue): JsResult[LocalDateTime] =
      implicitly[Reads[JsArray]].reads(json).map {
        case JsArray(
        Seq(JsNumber(y),
        JsNumber(m),
        JsNumber(d),
        JsNumber(h),
        JsNumber(mm),
        JsNumber(ss))) =>
          new LocalDateTime(y.intValue(),
            m.intValue(),
            d.intValue(),
            h.intValue(),
            mm.intValue(),
            ss.intValue())
      }
  }

  implicit val jodaLocalDateFormat = new Format[LocalDate] {
    override def writes(o: LocalDate): JsValue =
      JsArray(
        Seq(
          JsNumber(o.getYear),
          JsNumber(o.getMonthOfYear),
          JsNumber(o.getDayOfMonth)
        ))

    override def reads(json: JsValue): JsResult[LocalDate] =
      implicitly[Reads[JsArray]].reads(json).map {
        case JsArray(Seq(JsNumber(y), JsNumber(m), JsNumber(d))) =>
          new LocalDate(y.intValue(), m.intValue(), d.intValue())
      }
  }

  implicit val paragraphForma = Json.format[ParagraphRow]
  implicit val sectionFormat = Json.format[SectionRow]
  implicit val opportunityFormat = Json.format[OpportunityRow]

  implicit val oppDurFormat = Json.format[OpportunityDuration]
  implicit val oppValueFormat = Json.format[OpportunityValue]
  implicit val oppDescFormat = Json.format[OpportunityDescriptionSection]
  implicit val oppFormat = Json.format[Opportunity]
}
