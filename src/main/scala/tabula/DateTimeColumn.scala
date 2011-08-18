package tabula

import org.scala_tools.time.Imports._

case class DateTimeColumn(value: Option[DateTime]) extends Column with HasValue with DumbValueFormatter
