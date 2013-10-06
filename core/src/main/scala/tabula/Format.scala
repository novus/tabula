package tabula

import shapeless._
import Tabula._

trait Format extends Poly2 {
  type Base

  type CellT[C] = ColumnAndCell[_, _, C]

  trait Formatter[C] {
    type Local <: Base
    def apply(cell: Cell[C]): Local
    def apply(cell: CellT[C]): Local = apply(cell._2)
  }

  trait SimpleFormatter[C] extends Formatter[C] {
    type Local = Base
  }

  def apply[C](cell: Cell[C])(implicit fter: Formatter[C]) = fter(cell)

  type Row

  trait RowProto {
    def emptyRow: Row
    def appendCell[C](cell: CellT[C])(row: Row)(implicit fter: Formatter[C]): Row
  }

  val RowProto: RowProto

  implicit def caseRowCell[F, T, C](implicit fter: Formatter[C]) =
    at[Row, ColumnAndCell[F, T, C]]((r, c) => RowProto.appendCell(c)(r))
}
