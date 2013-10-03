package tabula

import Tabula._
import shapeless._
import shapeless.HList._
import shapeless.Poly._

object Tabula extends Cellulizers with Aggregators {
  type ColumnAndCell[F, T, C] = (Column[F, T, C], Cell[C])
  type ColFun[F, T, C] = F => ColumnAndCell[F, T, C]

  def cells[F, T, C, NcT <: HList, O <: HList, Col](cols: Col :: NcT)(implicit ev: Col <:< Column[F, T, C], aa: ApplyAll[F, Col :: NcT, ColumnAndCell[F, T, C] :: O]): F => ColumnAndCell[F, T, C] :: O =
    (x: F) => ApplyAll(x)(cols)

  implicit def pimpCells[F, T, C, O <: HList](cells: ColumnAndCell[F, T, C] :: O) = new {
    type Cells = ColumnAndCell[F, T, C] :: O
    def row[Fmt <: Format](format: Fmt)(implicit ev: Fmt <:< Format, lf: LeftFolder[Cells, format.Row, Fmt]) =
      cells.foldLeft(format.RowOps.emptyRow)(format)
  }
}

trait Cell[A] {
  def value: Option[A]
  def m: Manifest[A]
}

object Column {
  implicit def optionize[T](t: T): Option[T] = Option(t)
}

abstract class Column[F, T, C](val f: F => Option[T])(implicit val cz: Cellulizer[T, C], val mf: Manifest[F], val mc: Manifest[C]) extends ColFun[F, T, C] {
  def apply(source: F): ColumnAndCell[F, T, C] = this -> cz(source, f)

  class Transform[TT, CC](next: Column[T, TT, CC])(implicit mcc: Manifest[CC]) extends Column[F, TT, CC](f(_).flatMap(next.f))(next.cz, mf, mcc)

  def |[TT, CC](next: Column[T, TT, CC])(implicit mcc: Manifest[CC]) = new Transform[TT, CC](next)

  def `@@`(name: String) = new NamedColumn(cellulize(name), this)
}

class NamedColumn[F, T, C, Col](val name: Cell[String], column: Col)(implicit ev: Col <:< Column[F, T, C]) extends Column[F, T, C](column.f)(column.cz, column.mf, column.mc) with ColFun[F, T, C]
