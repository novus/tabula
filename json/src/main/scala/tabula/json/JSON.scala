package tabula.json

import tabula._
import Tabula._
import com.github.nscala_time.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }
import org.json4s._
import org.json4s.native.JsonMethods._
import shapeless._
import java.io.{ OutputStream, PrintWriter }

trait JSON extends Format {
  type Base = JValue

  implicit object StringFormatter extends Formatter[String] {
    type Local = JString
    def apply(cell: Cell[String]) = JString(cell.value.getOrElse(""))
  }

  implicit object DateTimeFormatter extends Formatter[DateTime] {
    type Local = JInt
    def apply(cell: Cell[DateTime]) = cell.value.map(dt => JInt(dt.getMillis)).getOrElse(JInt(0))
  }

  implicit object BigDecimalFormatter extends Formatter[ScalaBigDecimal] {
    type Local = JDecimal
    def apply(cell: Cell[ScalaBigDecimal]) = JDecimal(cell.value.getOrElse(ScalaBigDecimal(0)))
  }

  type Row = JArray

  object RowProto extends RowProto {
    def emptyRow = JArray(Nil)
    def appendCell[C](cell: CellT[C])(row: JArray)(implicit fter: Formatter[C]) =
      row.copy(arr = row.arr ::: fter(cell._2) :: Nil)
  }

  def writer(names: List[Option[String]]) = new WriterSpawn(names) {
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
}
