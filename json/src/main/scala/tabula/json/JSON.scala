package tabula.json

import tabula._
import org.scala_tools.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

trait JSON extends Output {
  type CellForm = JValue

  implicit object StringFormat extends Format[String] {
    type Form = JString
    def apply(x: Option[String]) = JString(x.getOrElse(""))
  }

  implicit object DateTimeFormat extends Format[DateTime] {
    type Form = JInt
    def apply(x: Option[DateTime]) =
      x.map(dt => JInt(dt.getMillis)).getOrElse(JInt(0))
  }

  implicit object BigDecimalFormat extends Format[BigDecimal] {
    type Form = JDouble
    def apply(x: Option[ScalaBigDecimal]) =
      JDouble(x.getOrElse(BigDecimal(0)).doubleValue)
  }

  type RowForm = JArray
  def apply(row: Row) = apply(row.cells)

  type TableForm = JArray
  def apply(table: Table) = table.rows.map(apply)
}

object JSON extends JSON
