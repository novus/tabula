package tabula

object StringCell {
  def apply(o: Option[String]): StringCell = apply(o.getOrElse(""))
}
case class StringCell(value: String) extends Cell with HasValue with DumbValueFormatter with LinkableCell {
  def linkTo(u: String, t: Option[String] = None, cs: List[String] = Nil) = {
    new StringCell(value) with LinkedCell {
      val url = u
      val text = t
      val classes = cs
    }
  }
}
