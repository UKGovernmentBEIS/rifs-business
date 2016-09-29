package rifs.slicks.modulesdefs

import rifs.models.{OpportunityRow, ParagraphRow, SectionRow}
import rifs.slicks.gen.{ModuleDefinition, ModuleSpec}

object OpportunityModuleDef extends ModuleDefinition {
  val spec = ModuleSpec("OpportunityModule")
    .withTableFor[OpportunityRow]
    .withTableFor[SectionRow]
    .withTableFor[ParagraphRow]
}
