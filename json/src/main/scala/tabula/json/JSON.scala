package tabula.json

import tabula._
import org.scala_tools.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

abstract class JSON extends Output[JValue] {
  protected def string(cell: Cell[String]) = JString(cell.value.getOrElse(""))
  protected def dateTime(cell: Cell[DateTime]) = cell.value.map(dt => JInt(dt.getMillis)).getOrElse(JInt(0))
  protected def bigDecimal(cell: Cell[BigDecimal]) = JDouble(cell.value.getOrElse(BigDecimal(0)).doubleValue)
}

object JSON extends JSON
