package tabula

import Tabula._
import shapeless._
import shapeless.ops.hlist._

object Tabula extends Cellulizers with Aggregators {
  type ColumnAndCell[F, T, C] = (Column[F, T, C], Cell[C])
  type ColFun[F, T, C] = F => ColumnAndCell[F, T, C]

  def cells[F, T, C, NcT <: HList, O <: HList, Col](cols: Col :: NcT)(implicit ev: Col <:< Column[F, T, C], aa: ApplyAll[F, Col :: NcT, ColumnAndCell[F, T, C] :: O]): F => ColumnAndCell[F, T, C] :: O =
    (x: F) => ApplyAll(x)(cols)

  implicit def pimpCells[F, T, C, O <: HList](cells: ColumnAndCell[F, T, C] :: O) = new {
    type Cells = ColumnAndCell[F, T, C] :: O
    def row[Fmt <: Format](format: Fmt)(implicit ev: Fmt <:< Format, lf: LeftFolder[Cells, format.Row, format.type]): format.Row =
      cells.foldLeft(format.RowProto.emptyRow)(format)(lf).asInstanceOf[format.Row]
  }
}
