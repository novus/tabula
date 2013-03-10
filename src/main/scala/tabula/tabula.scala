package tabula

import Tabula._
import shapeless._
import shapeless.HList._
import shapeless.Poly._

object Tabula extends Cellulizers with Aggregators {
  implicit def optionize[T](t: T): Option[T] = Option(t)
  implicit def nameColumn[F, T, C](args: (String, Column[F, T, C])) = NamedColumn(cellulize(args._1), args._2)
  implicit def ncspimp[F, T, C, NcT <: HList](ncs: NamedColumn[F, T, C] :: NcT) = new {
    def |:[TT, CC](next: NamedColumn[F, TT, CC]) = next :: ncs
  }
  def row[F, T, C, NcT <: HList, O <: HList](cols: NamedColumn[F, T, C] :: NcT)(implicit aa: ApplyAll[F, tabula.NamedColumn[F, T, C] :: NcT, Cell[C] :: O]): F => Cell[C] :: O = {
    import ApplyAll._
    x => applyAllTo(x)(cols)
  }
}

trait Cell[A] {
  def value: Option[A]
  def m: Manifest[A]
}

abstract class Column[F, T, C](val f: F => Option[T])(implicit val cz: Cellulizer[T, C]) extends (F => Cell[C]) {
  def apply(x: F): Cell[C] = (f andThen cz.apply)(x)

  abstract class Transform[F, T, C1, TT, C2](
    val left: Column[F, T, C1],
    val right: Column[T, TT, C2]) extends Column[F, TT, C2](left.f(_).flatMap(right.f))(right.cz)

  def |[TT, CC](right: Column[T, TT, CC]) =
    new Transform[F, T, C, TT, CC](this, right) {}
}

case class NamedColumn[F, T, C](name: Cell[String], column: Column[F, T, C]) extends Column[F, T, C](column.f)(column.cz) with (F => Cell[C]) {
  def |:[TT, CC](next: NamedColumn[F, TT, CC]) = next :: this :: HNil
}
