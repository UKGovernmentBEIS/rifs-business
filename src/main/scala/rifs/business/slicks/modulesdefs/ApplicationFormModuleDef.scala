package rifs.business.slicks.modulesdefs

import rifs.business.models.{ApplicationFormQuestionRow, ApplicationFormRow, ApplicationFormSectionRow}
import rifs.slicks.gen.{ModuleDefinition, ModuleSpec}

object ApplicationFormModuleDef extends ModuleDefinition {
  val spec = ModuleSpec("ApplicationFormModule")
    .withTableFor[ApplicationFormRow]
    .withTableFor[ApplicationFormSectionRow]
    .withTableFor[ApplicationFormQuestionRow]
    .dependsOn(OpportunityModuleDef)
}
