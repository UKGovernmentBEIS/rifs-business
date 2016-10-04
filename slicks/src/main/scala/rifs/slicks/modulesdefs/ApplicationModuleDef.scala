package rifs.slicks.modulesdefs

import rifs.models._
import rifs.slicks.gen.{ModuleDefinition, ModuleSpec}

object ApplicationModuleDef extends ModuleDefinition {
  val spec = ModuleSpec("ApplicationModule")
    .withTableFor[ApplicationRow]
    .withTableFor[ApplicationSectionRow]
    .dependsOn(OpportunityModuleDef)
}
