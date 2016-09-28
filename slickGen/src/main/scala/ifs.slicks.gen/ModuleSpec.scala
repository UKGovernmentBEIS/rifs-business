package ifs.slicks.gen

import com.wellfactored.propertyinfo.PropertyInfo
import shapeless.Typeable

case class ModuleSpec(name: String, tables: Seq[TableGen] = Seq(), dependsOn: Seq[ModuleDefinition] = Seq()) {

  implicit class StringSyntax(s: String) {
    def indent(n: Int): String = List.fill(n)(' ').mkString + s
  }

  implicit class StringSeqSyntax(ss: Seq[String]) {
    def indent(n: Int): Seq[String] = ss.map(_.indent(n))
  }

  lazy val importedTypeMappers: Seq[TypeMapper] = dependsOn.flatMap(_.spec.typeMappers)

  val typeMappers =
    tables
      .flatMap(_.typeMappers)
      .distinct
      .filter(tm => !importedTypeMappers.contains(tm))

  def generate: Seq[String] = {
    val selfTypes: Seq[String] = "DBBinding" +: dependsOn.map(_.spec.name)
    val traitDef = Seq(
      s"trait $name {",
      s"  self: ${selfTypes.mkString(" with ")} =>",
      s"  import driver.api._"
    )

    val tableDefs = tables.flatMap("" +: _.genTable())

    val foot = Seq("}")

    (traitDef :: typeMappers.map(_.asString).indent(2) :: tableDefs.indent(2) :: foot :: Nil).flatten
  }

  def withTable[T](t: TableGenerator[T]) = copy(tables = t +: tables)

  def withTableFor[T](implicit ty: Typeable[T], ti: PropertyInfo[T]) = copy(tables = new TableGenerator[T] +: tables)

  def dependsOn(mods: ModuleDefinition*): ModuleSpec = copy(dependsOn = mods ++: dependsOn)
}

