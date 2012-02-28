package tabula

object StringCell {
  def apply(o: Option[String]): StringCell = apply(o.getOrElse(""))
}
case class StringCell(value: String) extends Cell
