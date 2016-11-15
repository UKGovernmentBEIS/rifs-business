package rifs.business.slicks.modulesdefs

import rifs.business.models.{OpportunityRow, SectionRow}
import rifs.slicks.gen.{ModuleDefinition, ModuleSpec}

object OpportunityModuleDef extends ModuleDefinition {
  val spec = ModuleSpec("OpportunityModule")
    .withTableFor[OpportunityRow]
    .withTableFor[SectionRow]
}
