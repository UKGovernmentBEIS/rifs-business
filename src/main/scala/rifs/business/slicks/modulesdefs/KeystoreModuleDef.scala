package rifs.business.slicks.modulesdefs

import rifs.business.models.KeystoreRow
import rifs.slicks.gen.{ModuleDefinition, ModuleSpec}

object KeystoreModuleDef extends ModuleDefinition {
  val spec = ModuleSpec("KeystoreModule")
    .withTableFor[KeystoreRow]
}
