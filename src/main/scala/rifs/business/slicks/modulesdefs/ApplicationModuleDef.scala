package rifs.business.slicks.modulesdefs

import com.wellfactored.slickgen.{ModuleDefinition, ModuleSpec}
import rifs.business.models._

object ApplicationModuleDef extends ModuleDefinition {
  val spec = ModuleSpec("ApplicationModule")
    .withTableFor[ApplicationRow]
    .withTableFor[ApplicationSectionRow]
    .dependsOn(ApplicationFormModuleDef)
}
