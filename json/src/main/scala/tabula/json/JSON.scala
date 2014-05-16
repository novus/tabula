package tabula.json

import tabula._
import Tabula._
import org.joda.time._
import org.json4s._
import org.json4s.native.JsonMethods._
import shapeless._
import java.io.{ OutputStream, PrintWriter }

trait JSON extends Format {
  type Base = JValue

  implicit object StringFormatter extends Formatter[String] {
    type Local = JString
    def apply(value: Option[String]) = JString(value.getOrElse("")) :: Nil
  }

  implicit object DateTimeFormatter extends Formatter[DateTime] {
    type Local = JInt
    def apply(value: Option[DateTime]) = value.map(dt => JInt(dt.getMillis)).getOrElse(JInt(0)) :: Nil
  }

  implicit object DoubleFormatter extends Formatter[Double] {
    type Local = JDouble
    def apply(value: Option[Double]) = JDouble(value.getOrElse(0d)) :: Nil
  }

  type Row = JArray

  object RowProto extends RowProto {
    def emptyRow = JArray(Nil)
    def appendCell[C](cell: Cell[C])(row: JArray)(implicit fter: Formatter[C]) =
      row.copy(arr = row.arr ::: fter(cell))
    def appendBase[T <: Base](value: T)(row: Row) =
      row.copy(arr = row.arr ::: value :: Nil)
  }

  class Spawn(names: List[Option[String]]) extends WriterSpawn(names) {
    def toStream(out: OutputStream) = new Writer(out) {
      val pw = new PrintWriter(out)

      override def start() {
        pw.print("[")
        pw.print(pretty(render(RowProto.header(names))))
      }

      def writeMore(rows: Iterator[Row]) =
        rows.map(render).map(pretty).map(","+_).foreach(pw.print)

      override def finish() {
        pw.print("]")
        pw.flush()
      }
    }
  }

  def writer(names: List[Option[String]]) = new Spawn(names)
}
