package tabula

import org.scala_tools.time.Imports._
import org.joda.time.format.DateTimeFormatter

case class DateTimeCell(value: Option[DateTime], formatter: Option[DateTimeFormatter] = None) extends Cell with HasValue {
  lazy val format = value.flatMap(dt => formatter.map(_.print(dt))).getOrElse("")
}
