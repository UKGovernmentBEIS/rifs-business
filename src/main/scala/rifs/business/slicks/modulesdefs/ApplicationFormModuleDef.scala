package rifs.business.slicks.modulesdefs

import com.wellfactored.slickgen._
import rifs.business.models.{ApplicationFormQuestionRow, ApplicationFormRow, ApplicationFormSectionRow}

object ApplicationFormModuleDef extends ModuleDefinition {
  val spec = ModuleSpec("ApplicationFormModule")
    .withTableFor[ApplicationFormRow]
    .withTableFor[ApplicationFormSectionRow]
    .withTableFor[ApplicationFormQuestionRow]
    .dependsOn(OpportunityModuleDef)
}
