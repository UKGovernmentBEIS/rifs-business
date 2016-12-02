package rifs.business.tables

import javax.inject.Inject

import com.github.tminglei.slickpg.{ExPostgresDriver, PgDateSupportJoda, PgPlayJsonSupport}
import play.api.db.slick.DatabaseConfigProvider
import rifs.business.data.ApplicationFormOps
import rifs.business.models._
import rifs.business.restmodels.{ApplicationForm, ApplicationFormSection, Question}
import rifs.business.slicks.modules.{ApplicationFormModule, OpportunityModule, PgSupport}
import rifs.business.slicks.support.DBBinding

import scala.concurrent.{ExecutionContext, Future}

class ApplicationFormTables @Inject()(override val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends ApplicationFormModule
    with OpportunityModule
    with ApplicationFormOps
    with DBBinding
    with PgSupport {

  import ApplicationFormExtractors._
  import driver.api._

  override def byId(id: ApplicationFormId): Future[Option[ApplicationForm]] = db.run {
    byIdWithSectionsC(id).result.map { rs =>
      val (as, ss) = rs.unzip
      as.map(a => ApplicationForm(a.id, a.opportunityId, a.id.map(sectionsFor(_, ss)).getOrElse(List.empty)))
    }.map(_.headOption)
  }

  override def forOpportunity(opportunityId: OpportunityId): Future[Option[ApplicationForm]] = db.run {
    applicationByOppIdC(opportunityId).result.map { rs =>
      val (as, ss) = rs.unzip
      as.map(a => ApplicationForm(a.id, a.opportunityId, a.id.map(sectionsFor(_, ss)).getOrElse(List.empty)))
    }.map(_.headOption)
  }

  /*
    ******************************
    * Queries and compiled queries
     */
  def byIdQ(id: Rep[ApplicationFormId]): ApplicationFormQuery = for {a <- applicationFormTable if a.id == id} yield a

  val byIdC = Compiled(byIdQ _)

  type ApplicationWithSectionsJoin = Query[(ApplicationFormTable, (ApplicationFormSectionTable, Rep[Option[ApplicationFormQuestionTable]])), (ApplicationFormRow, (ApplicationFormSectionRow, Option[ApplicationFormQuestionRow])), Seq]

  def byIdWithSectionsQ(id: Rep[ApplicationFormId]): ApplicationWithSectionsJoin = for {
    a <- applicationFormTable if a.id === id
    sq <- applicationFormSectionTable joinLeft applicationFormQuestionTable on (_.id === _.applicationFormSectionId) if sq._1.applicationFormId === a.id
  } yield (a, sq)

  val byIdWithSectionsC = Compiled(byIdWithSectionsQ _)

  def applicationByOppIdQ(opportunityId: Rep[OpportunityId]): ApplicationWithSectionsJoin = for {
    a <- applicationFormTable if a.opportunityId === opportunityId
    sq <- applicationFormSectionTable joinLeft applicationFormQuestionTable on (_.id === _.applicationFormSectionId) if sq._1.applicationFormId === a.id
  } yield (a, sq)

  val applicationByOppIdC = Compiled(applicationByOppIdQ _)
}

object ApplicationFormExtractors {
  def sectionsFor(applicationFormId: ApplicationFormId, sectionRows: Seq[(ApplicationFormSectionRow, Option[ApplicationFormQuestionRow])]): Seq[ApplicationFormSection] = {
    val sections = buildSections(sectionRows)
    val sectionIds = sectionRows.filter(_._1.applicationFormId == applicationFormId).map(_._1.id.get)
    sectionIds.distinct.flatMap(sections.get)
  }

  def buildSections(sectionRows: Seq[(ApplicationFormSectionRow, Option[ApplicationFormQuestionRow])]): Map[ApplicationFormSectionId, ApplicationFormSection] = {
    val (sections, questions) = sectionRows.unzip
    val ps = sections.distinct.map { s =>
      val qs = questions.flatten.filter(_.applicationFormSectionId == s.id).map(q => Question(q.key, q.text, q.description, q.helpText))
      s.id.get -> ApplicationFormSection(s.sectionNumber, s.title, qs, s.sectionType, s.fields)
    }
    Map(ps: _*)
  }
}