package rifs.business.slicks.modulesdefs

import rifs.business.models._
import rifs.slicks.gen.{ModuleDefinition, ModuleSpec}

object ApplicationModuleDef extends ModuleDefinition {
  val spec = ModuleSpec("ApplicationModule")
    .withTableFor[ApplicationRow]
    .withTableFor[ApplicationSectionRow]
    .dependsOn(ApplicationFormModuleDef)
}
