package ifs.slicks.gen

import com.wellfactored.propertyinfo.{Property, PropertyInfo, PropertyInfoGen}
import shapeless.Typeable

trait StringOps {
  def decamelise(s: String) = s.replaceAll("([a-z])([A-Z])", "$1_$2")

  def lowerCaseFirst(s: String): String = s.substring(0, 1).toLowerCase + s.substring(1)

  def stripFromEnd(s: String, count: Int) = s.substring(0, s.length - count)
}

trait TableGen {
  def genTable(): Seq[String]

  def typeMappers: Seq[TypeMapper]
}

class TableGenerator[T](implicit ty: Typeable[T], pi: PropertyInfo[T]) extends TableGen with StringOps with PropertyInfoGen {
  val namesAndTypes: Seq[Property[_]] = pi.namesAndTypes
  val cols = namesAndTypes.map(TableColumn(_))
  val typeMappers: Seq[TypeMapper] = cols.flatMap(_.typeMapper).distinct

  val name = ty.describe
  val root = stripFromEnd(name, 3)
  val tableSQLName = decamelise(root).toLowerCase
  val tableClassName = s"${root}Table"
  val classDef = s"""class $tableClassName(tag: Tag) extends Table[$name](tag, "$tableSQLName")"""

  def generateDefsForColumn(col: TableColumn[_]): Seq[String] = {
    val colOpts = if (col.opts.isEmpty) "" else s""", ${col.opts.mkString(", ")}"""
    val colDef = s"""def ${col.prop.name} = column[${col.prop.ty.describe}]("${col.sqlName}"$colOpts)"""

    if (col.isIdType && !col.sqlName.equals("id")) {
      val indexRoot = col.sqlName.replaceAll("_id", "")
      val fkSQLName = s"${root.toLowerCase}_${indexRoot.toLowerCase}_fk"
      val idxSQLName = s"${root.toLowerCase}_${indexRoot.toLowerCase}_idx"
      val idStripped = col.prop.name.replaceAll("Id$", "")
      val referencedTableName = lowerCaseFirst(stripFromEnd(col.prop.ty.describe, 2))
      val identifierRoot = lowerCaseFirst(col.prop.name)
      val fk = s"""def ${identifierRoot}FK = foreignKey("$fkSQLName", ${col.prop.name}, ${referencedTableName + "Table"})(_.id, onDelete = ForeignKeyAction.Cascade)"""
      val index = s"""def ${identifierRoot}Index = index("$idxSQLName", ${col.prop.name})"""
      Seq(colDef, fk, index)
    } else Seq(colDef)
  }


  lazy val genTable: Seq[String] = {
    val colDefs = cols.flatMap(generateDefsForColumn)

    val starDef = s"def * = (${namesAndTypes.map(_.name).mkString(", ")}) <> ($name.tupled, $name.unapply)"

    Seq(
      Seq(queryAlias, classDef + " {"),
      Seq(colDefs.map(d => "    " + d): _*),
      Seq("    " + starDef, "}", tableVal)
    ).flatten
  }


  lazy val tableVal = s"lazy val ${lowerCaseFirst(root)}Table = TableQuery[$tableClassName]"

  val queryAlias = s"type ${root}Query = Query[${root}Table, $name, Seq]"

}

object TableGenerator extends PropertyInfoGen {
  def forClass[T](implicit ty: Typeable[T], ti: PropertyInfo[T]) = new TableGenerator[T]
}



