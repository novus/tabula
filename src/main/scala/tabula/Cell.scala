package tabula

import com.novus.salat.annotations._

@Salat
trait Cell {
  def format: String
}

trait LinkedCell extends Cell {
  val url: String
  val text: Option[String]
  val classes: List[String]
}

trait LinkableCell extends Cell {
  def linkTo(url: String, text: Option[String], classes: List[String]): LinkedCell
}

trait HasValue {
  val value: Any
}

trait DumbValueFormatter {
  self: HasValue =>
  def format: String = "\"%s\"".format((value match {
    case o: Option[Any] => o
    case _              => Option(value)
  }).map(_.toString).getOrElse("").replaceAll("\"", "\"\""))
}
