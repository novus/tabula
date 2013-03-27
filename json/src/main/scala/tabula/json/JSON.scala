package tabula.json

import tabula._
import com.github.nscala_time.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }
import org.json4s._
import org.json4s.native.JsonMethods._

abstract class JSON extends Output[JField] {
  protected def string(cell: Cell[String]) = JString(cell.value.getOrElse(""))
  protected def dateTime(cell: Cell[DateTime]) = cell.value.map(dt => JInt(dt.getMillis)).getOrElse(JInt(0))
  protected def bigDecimal(cell: Cell[BigDecimal]) = JDouble(cell.value.getOrElse(BigDecimal(0)).doubleValue)
}

object JSON extends JSON
