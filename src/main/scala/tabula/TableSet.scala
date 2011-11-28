package tabula

trait AsTableSet {
  val name: String
  val tables: List[Table]
}

object TableSet {
  val empty = TableSet(name = "", tables = Nil)
}

case class TableSet(name: String, tables: List[Table]) extends AsTableSet
