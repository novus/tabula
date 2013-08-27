package tabula

import Tabula._
import org.joda.time.DateTime
import scala.math.{ BigDecimal => ScalaBigDecimal }

trait CSV extends Format {
  type Base = String

  implicit object StringFormatter extends SimpleFormatter[String] {
    def scrub(x: Option[String]) = {
      x
        .flatMap(Option(_))
        .map(_.trim)
        .filterNot(_ == "")
    }

    def quote(x: Option[String]) = {
      scrub(x)
        .map("\"%s\"".format(_))
        .getOrElse("")
    }

    def apply(cell: Cell[String]) = quote(cell.value)
  }

  class DateTimeFormatter(df: => org.joda.time.format.DateTimeFormatter) extends SimpleFormatter[DateTime] {
    def apply(cell: Cell[DateTime]) = StringFormatter.quote(cell.value.map(df.print))
  }

  class BigDecimalFormatter(df: => java.text.DecimalFormat) extends SimpleFormatter[ScalaBigDecimal] {
    def apply(cell: Cell[ScalaBigDecimal]) = StringFormatter.scrub(cell.value.map(df.format)).getOrElse("")
  }

  type Row = String

  object RowOps extends RowOps {
    def emptyRow = ""
    def appendCell[C](cell: CellT[C])(row: String)(implicit fter: Formatter[C]) = {
      val base = fter(cell)
      if (row == emptyRow) base
      else s"${row},${base}"
    }
  }
}

object CSV {
  abstract class default extends CSV {
    implicit val DateTimeFormatter = new DateTimeFormatter(org.joda.time.format.DateTimeFormat.fullDateTime)
    implicit val BigDecimalFormatter = new BigDecimalFormatter(new java.text.DecimalFormat("#,##0.00;-#,##0.00"))
  }
}
