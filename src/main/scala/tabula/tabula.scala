package tabula

import Tabula._
import shapeless._
import shapeless.HList._
import shapeless.Poly._

object Tabula extends Cellulizers with Aggregators {
  type ColumnAndCell[F, T, C] = (Column[F, T, C], Cell[C])
  type ColFun[F, T, C] = F => ColumnAndCell[F, T, C]

  implicit def optionize[T](t: T): Option[T] = Option(t)

  def row[F, T, C, NcT <: HList, O <: HList, Col](cols: Col :: NcT)(implicit ev: Col <:< Column[F, T, C], aa: ApplyAll[F, Col :: NcT, ColumnAndCell[F, T, C] :: O]) =
    (x: F) => ApplyAll(x)(cols)

  def column[F, T, C, Col](col: Col)(implicit ev: Col <:< Column[F, T, C]): Column[F, T, C] = ev(col)
}

trait Cell[A] {
  def value: Option[A]
  def m: Manifest[A]
}

abstract class Column[F, T, C](val f: F => Option[T])(implicit val cz: Cellulizer[T, C], val mf: Manifest[F], val mc: Manifest[C]) extends ColFun[F, T, C] {
  def apply(x: F): ColumnAndCell[F, T, C] = this -> (f andThen cz)(x)

  class Transform[TT, CC](next: Column[T, TT, CC])(implicit mcc: Manifest[CC]) extends Column[F, TT, CC](f(_).flatMap(next.f))(next.cz, mf, mcc)

  def |[TT, CC](next: Column[T, TT, CC])(implicit mcc: Manifest[CC]) = new Transform[TT, CC](next)

  def `@@`(name: String) = new NamedColumn(cellulize(name), this)
}

class NamedColumn[F, T, C, Col](name: Cell[String], column: Col)(implicit ev: Col <:< Column[F, T, C]) extends Column[F, T, C](column.f)(column.cz, column.mf, column.mc) with ColFun[F, T, C]
