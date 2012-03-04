package tabula

import org.scala_tools.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }

trait CSV extends Output {
  type CellForm = String

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

  implicit object StringFormat extends Format[String] {
    type Form = String
    def apply(x: Option[String]) = quote(x)
  }

  protected def dateTimeFormat = org.joda.time.format.DateTimeFormat.fullDateTime

  implicit object DateTimeFormat extends Format[DateTime] {
    import org.joda.time.format.DateTimeFormatter
    type Form = String
    def apply(x: Option[DateTime]) = quote(x.map(dateTimeFormat.print))
  }

  protected def bigDecimalFormat = new java.text.DecimalFormat("#,##0.00;-#,##0.00")

  implicit object BigDecimalFormat extends Format[BigDecimal] {
    type Form = String
    def apply(x: Option[ScalaBigDecimal]) = scrub(x.map(bigDecimalFormat.format)).getOrElse("")
  }

  type RowForm = String
  def apply(row: Row) = apply(row.cells).mkString(",")

  type TableForm = String
  def apply(table: Table) = (table.rows ::: table.footer.toList).map(apply).mkString("\n")
}

object CSV extends CSV
