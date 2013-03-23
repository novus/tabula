package tabula

import Tabula._
import shapeless._
import shapeless.HList._
import shapeless.Poly._

object Tabula extends Cellulizers with Aggregators {
  type ColumnAndCell[F, T, C] = (Column[F, T, C], Cell[C])
  type ColFun[F, T, C] = F => ColumnAndCell[F, T, C]

  implicit def optionize[T](t: T): Option[T] = Option(t)
  implicit def ncspimp[F, T, C, NcT <: HList](ncs: NamedColumn[F, T, C] :: NcT) = new {
    def |:[TT, CC](next: NamedColumn[F, TT, CC]) = next :: ncs
  }
  def row[F, T, C, NcT <: HList, O <: HList](cols: NamedColumn[F, T, C] :: NcT)(implicit aa: ApplyAll[F, NamedColumn[F, T, C] :: NcT, ColumnAndCell[F, T, C] :: O]) =
    (x: F) => ApplyAll(x)(cols)
}

trait Cell[A] {
  def value: Option[A]
  def m: Manifest[A]
}

abstract class Column[F, T, C](val f: F => Option[T])(implicit val cz: Cellulizer[T, C], val mf: Manifest[F], val mc: Manifest[C]) extends ColFun[F, T, C] {
  def apply(x: F): ColumnAndCell[F, T, C] = this -> (f andThen cz)(x)

  class Transform[TT, CC](next: Column[T, TT, CC])(implicit mcc: Manifest[CC]) extends Column[F, TT, CC](f(_).flatMap(next.f))(next.cz, mf, mcc)

  def |[TT, CC](next: Column[T, TT, CC])(implicit mcc: Manifest[CC]) = new Transform[TT, CC](next)

  def `@@`(name: String) = new NamedColumn[F, T, C](cellulize(name), this)
}

case class NamedColumn[F, T, C](name: Cell[String], column: Column[F, T, C]) extends Column[F, T, C](column.f)(column.cz, column.mf, column.mc) with ColFun[F, T, C] {
  def |:[TT, CC](next: NamedColumn[F, TT, CC]) = next :: this :: HNil
}
