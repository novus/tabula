package tabula

import Tabula._
import shapeless._
import shapeless.HList._
import shapeless.Poly._

object Tabula extends Cellulizers {
  implicit def optionize[T](t: T): Option[T] = Option(t)
  implicit def nameColumn[F, T, C](args: (String, Column[F, T, C])) = NamedColumn(cellulize(args._1), args._2)
  implicit def nameColumn2[F, T, C](args: (String, Column[F, T, C]))(implicit m: Manifest[C], cm: ConstMapperAux[F, NamedColumn2[F, T, C] :: HNil, F :: HNil]) = NamedColumn2(cellulize(args._1), args._2)(cm)
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

case class TableModel[F](private val row: RowModel[F], private val agg: Map[Column[_, _, _], Aggregator[_, _, _]] = Map.empty) {
  def apply(fs: List[F]): Table = Table(rows = fs.map(row(_)))
}

// ################################################################################
case class RowModel[F](private[tabula] val columns: List[NamedColumn[F, _, _]]) {
  def apply(f: F): Row = Row(columns.map(_(f)))
  def apply(fs: List[F]): List[Row] = fs.map(apply)
  def &(next: NamedColumn[F, _, _]) = copy(columns :+ next)
  def ++(other: RowModel[F]) = copy(columns ::: other.columns)
}

case class NamedColumn[F, T, C](name: Cell[String], column: Column[F, T, C]) extends Column[F, T, C](column.f)(column.cz) {
  def &[TT, CC](next: NamedColumn[F, TT, CC]) = RowModel[F](this :: next :: Nil)
}

// ################################################################################

case class NamedColumn2[F, T, C](name: Cell[String], column: Column[F, T, C])(implicit val cm: ConstMapperAux[F, NamedColumn2[F, T, C] :: HNil, F :: HNil]) extends Column[F, T, C](column.f)(column.cz) {
  def !:[TT, CC](next: NamedColumn2[F, TT, CC])(implicit mcc: Manifest[CC], cm2: ConstMapperAux[F, NamedColumn2[F, TT, CC] :: NamedColumn2[F, T, C] :: HNil, F :: F :: HNil]) =
    next !: RowModel2[F, T, C, NamedColumn2[F, T, C], HNil, Cell[C], HNil, HNil](this :: HNil)(cm)
}

object ColToF extends Poly {
  implicit def default[F, T, C] = case1[(NamedColumn2[F, T, C], F)] { case (col, x) => col(x) }
}

case class RowModel2[F, T, C, InH <: NamedColumn2[F, T, C], InT <: HList, OutH <: Cell[C], OutT <: HList, FsT <: HList](
    columns: InH :: InT)(implicit val cm: ConstMapperAux[F, InH :: InT, F :: FsT]) {

  def apply(x: F)(implicit zipper: Zip[(InH :: InT) :: (F :: FsT) :: HNil]) =
    columns.zip(columns.mapConst(x))

  def !:[TT, CC](next: NamedColumn2[F, TT, CC])(implicit mcc: Manifest[CC], cm3: ConstMapperAux[F, NamedColumn2[F, TT, CC] :: InH :: InT, F :: F :: FsT]) =
    RowModel2[F, TT, CC, NamedColumn2[F, TT, CC], InH :: InT, Cell[CC], OutH :: OutT, F :: FsT](next :: columns)
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
