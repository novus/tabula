package tabula

import shapeless._
import Tabula._
import org.joda.time.DateTime
import java.io.{ OutputStream, PrintWriter }

class CSV extends Format {
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

  class DoubleFormatter(df: => java.text.DecimalFormat) extends SimpleFormatter[Double] {
    def apply(cell: Cell[Double]) = StringFormatter.scrub(cell.value.map(df.format)).getOrElse("")
  }

  type Row = String

  object RowProto extends RowProto {
    def emptyRow = ""
    def appendCell[C](cell: CellT[C])(row: String)(implicit fter: Formatter[C]) = {
      val base = fter(cell)
      if (row == emptyRow) base
      else s"${row},${base}"
    }
  }

  class DefaultDateTimeFormatter extends DateTimeFormatter(org.joda.time.format.DateTimeFormat.fullDateTime)
  class DefaultDoubleFormatter extends DoubleFormatter(new java.text.DecimalFormat("#,##0.00;-#,##0.00"))

  def writer(names: List[Option[String]]) = new WriterSpawn(names) {
    def toStream(out: OutputStream) = new Writer(out) {
      lazy val pw = new PrintWriter(out)
      override def start() = pw.println(names.map(StringFormatter.quote).mkString(","))
      def writeMore(rows: Iterator[String]) = for (row <- rows) pw.println(row)
      override def finish() = pw.flush()
    }
  }
}
