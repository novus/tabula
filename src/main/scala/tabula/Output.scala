package tabula

import shapeless._
import com.github.nscala_time.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }

abstract class Output[CellForm] extends Pullback1[CellForm] {
  self =>
  def apply[C, CT <: HList](cells: Cell[C] :: CT)(
    implicit mapper: Mapper[Output[CellForm], Cell[C] :: CT]) =
    cells.map(self)

  protected def string(cell: Cell[String]): CellForm
  protected def dateTime(cell: Cell[DateTime]): CellForm
  protected def bigDecimal(cell: Cell[BigDecimal]): CellForm

  implicit def forString = at[Cell[String]](string)
  implicit def forDateTime = at[Cell[DateTime]](dateTime)
  implicit def forBigDecimal = at[Cell[BigDecimal]](bigDecimal)
}
