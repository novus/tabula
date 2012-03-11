package tabula

import Tabula._
import shapeless._
import shapeless.HList._
import shapeless.Poly._

object Tabula extends Cellulizers {
  implicit def optionize[T](t: T): Option[T] = Option(t)
  implicit def nameColumn[F, T, C](args: (String, Column[F, T, C])) = NamedColumn(cellulize(args._1), args._2)
  implicit def ncspimp[F, T, C, NcT <: HList](ncs: NamedColumn[F, T, C] :: NcT) = new {
    def |:[TT, CC](next: NamedColumn[F, TT, CC]) = next :: ncs
  }
  class Blank[T](implicit val m: Manifest[T]) extends Cell[T] {
    val value = None
  }
  def blank[T](implicit m: Manifest[T]) = new Blank[T]
  implicit def aggregator2agg[F, T, C](aggregator: Aggregator[F, T, C]): (Column[F, T, C], Aggregator[F, T, C]) = aggregator.col -> aggregator
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

  override lazy val hashCode = f.hashCode
  override def equals(x: Any): Boolean =
    x match {
      case other: Column[_, _, _] => other.hashCode == hashCode
      case _                      => false
    }
}

case class NamedColumn[F, T, C](name: Cell[String], column: Column[F, T, C]) extends Column[F, T, C](column.f)(column.cz) {
  def |:[TT, CC](next: NamedColumn[F, TT, CC]) = next :: this :: HNil
}

object Runner extends Poly1 {
  implicit def default[F, T, C] = at[(NamedColumn[F, T, C], F)] { case (col, x) => col(x) }
}

case class Row(cells: List[Cell[_]]) {
  def at(idx: Int) =
    if (cells.isDefinedAt(idx)) Option(cells(idx))
    else None
  def apply(idx: Int) = at(idx).getOrElse(blank)
}
case class Table(name: Option[String] = None, header: Option[Row] = None, rows: List[Row], footer: Option[Row] = None)

abstract class Aggregator[F, T, C] {
  type CellType = C
  def col: Column[F, T, C]
  def apply(cs: List[Cell[C]]): Cell[C]
}

abstract class Reduce[F, T, C](val col: Column[F, T, C])(f: (Option[C], Option[C]) => Option[T])(implicit cz: Cellulizer[T, C]) extends Aggregator[F, T, C] {
  def apply(cs: List[Cell[C]]): Cell[C] =
    cs.reduceLeft((a, b) => cellulize(f(a.value, b.value)))
}

abstract class Fold[F, T, C](val col: Column[F, T, C])(init: => T)(f: (Option[C], Option[C]) => Option[T])(implicit cz: Cellulizer[T, C]) extends Aggregator[F, T, C] {
  import cz._
  def apply(cs: List[Cell[C]]): Cell[C] =
    cs.foldLeft[Cell[C]](init)((a, b) => f(a.value, b.value))
}
