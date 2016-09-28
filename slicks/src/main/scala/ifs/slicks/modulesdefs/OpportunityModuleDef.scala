package ifs.slicks.modulesdefs

import ifs.models.{OpportunityRow, ParagraphRow, SectionRow}
import ifs.slicks.gen.{ModuleDefinition, ModuleSpec}

object OpportunityModuleDef extends ModuleDefinition {
  val spec = ModuleSpec("OpportunityModule")
    .withTableFor[OpportunityRow]
    .withTableFor[SectionRow]
    .withTableFor[ParagraphRow]
}
