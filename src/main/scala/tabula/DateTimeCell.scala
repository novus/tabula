package tabula

import org.scala_tools.time.Imports._
import org.joda.time.format.DateTimeFormatter

case class DateTimeCell(value: Option[DateTime], formatter: Option[DateTimeFormatter] = None) extends Cell {
  lazy val format =
    (value, formatter) match {
      case (Some(dt), Some(fmt)) => fmt.print(dt)
      case (Some(dt), _)         => "%s".format(dt)
      case _                     => ""
    }
}
