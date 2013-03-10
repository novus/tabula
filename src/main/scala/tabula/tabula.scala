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

abstract class Column[F, T, C](val f: F => Option[T])(implicit val cz: Cellulizer[T, C], val mf: Manifest[F], val mc: Manifest[C]) extends (F => Cell[C]) {
  def apply(x: F): Cell[C] = (f andThen cz.apply)(x)

  class Transform[TT, CC](
    val left: Column[F, T, C],
    val right: Column[T, TT, CC])(implicit mcc: Manifest[CC]) extends Column[F, TT, CC](left.f(_).flatMap(right.f))(right.cz, mf, mcc)

  def |[TT, CC](right: Column[T, TT, CC])(implicit mcc: Manifest[CC]) = new Transform[TT, CC](this, right)

  override def toString = "Column(%s -> %s)".format(mf.runtimeClass.getSimpleName, mc.runtimeClass.getSimpleName)
}

case class NamedColumn[F, T, C](name: Cell[String], column: Column[F, T, C]) extends Column[F, T, C](column.f)(column.cz, column.mf, column.mc) with (F => Cell[C]) {
  def |:[TT, CC](next: NamedColumn[F, TT, CC]) = next :: this :: HNil
  override def toString = "%s(%s)".format(column, name.value.getOrElse("N/A"))
}
