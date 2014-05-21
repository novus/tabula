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

    def apply(value: Option[String]) = quote(value) :: Nil
  }

  class DateTimeFormatter(df: => org.joda.time.format.DateTimeFormatter) extends SimpleFormatter[DateTime] {
    def apply(value: Option[DateTime]) = StringFormatter.quote(value.map(df.print)) :: Nil
  }

  class DoubleFormatter(df: => java.text.DecimalFormat) extends SimpleFormatter[Double] {
    def apply(value: Option[Double]) = StringFormatter.scrub(value.map(df.format)).getOrElse("") :: Nil
  }

  type Row = String

  object RowProto extends RowProto {
    def emptyRow = ""
    def appendCell[C](cell: Cell[C])(row: String)(implicit fter: Formatter[C]) =
      fter(cell).foldLeft(row)((acc, elem) => appendBase(elem)(acc))
    def appendBase[T <: Base](value: T)(row: Row) =
      if (row == emptyRow) value
      else row+","+value
  }

  class DefaultDateTimeFormatter extends DateTimeFormatter(org.joda.time.format.DateTimeFormat.fullDateTime)
  class DefaultDoubleFormatter extends DoubleFormatter(new java.text.DecimalFormat("#,##0.00;-#,##0.00"))

  class Spawn(names: List[Option[String]]) extends WriterSpawn(names) {
    def toStream(out: OutputStream) = new Writer {
      lazy val pw = new PrintWriter(out)
      override def start() = pw.println(names.map(StringFormatter.quote).mkString(","))
      def writeMore(rows: Iterator[String]) = for (row <- rows) pw.println(row)
      override def finish() = pw.flush()
    }
  }

  def writer(names: List[Option[String]]) = new Spawn(names)
}
