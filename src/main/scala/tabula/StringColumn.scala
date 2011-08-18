package tabula

object StringColumn {
  def apply(o: Option[String]): StringColumn = apply(o.getOrElse(""))
}
case class StringColumn(value: String) extends Column with HasValue with DumbValueFormatter with LinkableColumn {
  def linkTo(u: String, t: Option[String] = None, cs: List[String] = Nil) = {
    new StringColumn(value) with LinkedColumn {
      val url = u
      val text = t
      val classes = cs
    }
  }
}
