package tabula

trait Cell

trait LinkedCell extends Cell {
  val url: String
  val text: Option[String]
  val classes: List[String]
}

trait LinkableCell extends Cell {
  def linkTo(url: String, text: Option[String], classes: List[String]): LinkedCell
}
