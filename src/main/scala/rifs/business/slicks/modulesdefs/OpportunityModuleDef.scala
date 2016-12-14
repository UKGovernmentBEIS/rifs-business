package rifs.business.slicks.modulesdefs

import com.wellfactored.slickgen.{ModuleDefinition, ModuleSpec}
import rifs.business.models.{OpportunityRow, SectionRow}

object OpportunityModuleDef extends ModuleDefinition {
  val spec = ModuleSpec("OpportunityModule")
    .withTableFor[OpportunityRow]
    .withTableFor[SectionRow]
}
