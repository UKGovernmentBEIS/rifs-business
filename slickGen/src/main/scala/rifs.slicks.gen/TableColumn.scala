package rifs.slicks.gen

import com.wellfactored.propertyinfo.Property

case class TypeMapper(memberName: String, columnType: String, baseType: String) {
  val asString = s"implicit def ${columnType}Mapper: BaseColumnType[$columnType] = MappedColumnType.base[$columnType, Long](_.$memberName, $columnType)"
}

case class TableColumn[T](prop: Property[T], unique: Boolean = false, overrideColumnType: Option[String] = None) extends StringOps {

  import prop._

  val tyName = ty.describe

  val isStringType = tyName match {
    case "String" | "Option[String]" | "NonBlankString" | "Option[NonBlankString]" => true
    case _ => false
  }

  val lengthOpt = name match {
    case _ if name == "id" || name.endsWith("Id") => Some("O.Length(IdType.length)")
    case _ if isStringType => Some("O.Length(255)")
    case _ => None
  }

  val pkOpt = if (name == "id") Some("O.PrimaryKey") else None

  val numOpt = tyName match {
    case "BigDecimal" | "Option[BigDecimal]" => Some( """O.SqlType("decimal(9, 2)")""")
    case _ => None
  }

  val knownTypes = Seq("Byte", "BigDecimal", "String", "Long", "Boolean", "Int", "Short", "NonBlankString", "PhoneNumber", "LocalDate", "LocalDateTime")

  val isOptionOfKnownType: Boolean = tyName.startsWith("Option[") && !needsTypeMapper(tyName.substring(7, tyName.length - 1))

  def isIdType: Boolean = tyName.endsWith("Id") || tyName.startsWith("IdType[")

  def needsTypeMapper(t: String): Boolean = !(knownTypes.contains(t) || isOptionOfKnownType)

  val needsTypeMapper: Boolean = needsTypeMapper(tyName)

  /*
   * Relies on a convention that the member name of a wrapper type is the same as the last
   * part of the type name. E.g. SenderKey(key:String) or SMSProviderName(name:String)
   * TODO: Only create type mappers for classes with a single member
   * TODO: Use type information to extract the name of the member
    */
  def typeMapper: Option[TypeMapper] = if (needsTypeMapper) {
    val t = tyName.replace("Option[", "").replace("]", "")
    val s = decamelise(t).toLowerCase
    val memberNameIndex = s.lastIndexOf("_")
    val memberName = s.substring(memberNameIndex + 1)
    Some(TypeMapper(memberName, t, "Long"))
  } else None

  val opts = Seq(lengthOpt, pkOpt, numOpt).flatten

  val sqlName = decamelise(name).toLowerCase + (if (isIdType && !name.toLowerCase.endsWith("id")) "_id" else "")
}
