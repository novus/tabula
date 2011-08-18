package tabula

import com.novus.salat.annotations._

@Salat
trait Column {
  def format: String
}

trait LinkedColumn extends Column {
  val url: String
  val text: Option[String]
  val classes: List[String]
}

trait LinkableColumn extends Column {
  def linkTo(url: String, text: Option[String], classes: List[String]): LinkedColumn
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
