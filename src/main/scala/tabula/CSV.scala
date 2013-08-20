package tabula

import org.joda.time.DateTime
import scala.math.{ BigDecimal => ScalaBigDecimal }

trait CSV extends Format[String] {
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
}

object CSV {
  object default extends CSV {
    implicit val DateTimeFormatter = new DateTimeFormatter(org.joda.time.format.DateTimeFormat.fullDateTime)
    implicit val BigDecimalFormatter = new BigDecimalFormatter(new java.text.DecimalFormat("#,##0.00;-#,##0.00"))
  }
}
