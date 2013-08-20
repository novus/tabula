package tabula.json

import tabula._
import com.github.nscala_time.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }
import org.json4s._

trait JSON extends Format[JValue] {
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
}
