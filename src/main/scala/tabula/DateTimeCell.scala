package tabula

import org.scala_tools.time.Imports._

case class DateTimeCell(value: Option[DateTime]) extends Cell with HasValue with DumbValueFormatter
