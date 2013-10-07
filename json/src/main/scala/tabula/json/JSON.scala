package tabula.json

import tabula._
import Tabula._
import com.github.nscala_time.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }
import org.json4s._
import org.json4s.native.JsonMethods._
import shapeless._

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

  def writer[F, T, C, NcT <: HList, Col](cols: Col :: NcT)(implicit ev: Col <:< Column[F, T, C], tl: ToList[Col :: NcT, Column[_, _, _]]) = new WriterSpawn(NamedColumn.names(cols)) {
    def toStream(out: java.io.OutputStream) = new Writer(out) {
      val pw = new java.io.PrintWriter(out)

      override def before() {
        pw.println("[")
        pw.println(pretty(render(RowProto.header(names)))+",")
      }

      def write(rows: Iterator[Row]) {
        before()
        rows.map(render).map(pretty).foreach(pw.println)
        after()
      }

      override def after() {
        pw.println("]")
        pw.flush()
      }
    }
  }
}
