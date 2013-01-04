package tabula

import com.github.nscala_time.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }

abstract class CSV extends Output[String] {
  protected def dateTimeFormat = org.joda.time.format.DateTimeFormat.fullDateTime
  protected def bigDecimalFormat = new java.text.DecimalFormat("#,##0.00;-#,##0.00")

  protected def string(cell: Cell[String]) = quote(cell.value)
  protected def dateTime(cell: Cell[DateTime]) = quote(cell.value.map(dateTimeFormat.print))
  protected def bigDecimal(cell: Cell[BigDecimal]) = scrub(cell.value.map(bigDecimalFormat.format)).getOrElse("")

  private def scrub(x: Option[String]) = {
    x
      .flatMap(Option(_))
      .map(_.trim)
      .filterNot(_ == "")
  }

  private def quote(x: Option[String]) = {
    scrub(x)
      .map("\"%s\"".format(_))
      .getOrElse("")
  }
}

object CSV extends CSV
