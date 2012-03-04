package tabula

import shapeless._
import shapeless.HList._
import shapeless.Poly._
import shapeless.TypeOperators._
import shapeless.Mapper._
import shapeless.MapperAux._

object Tabula extends Cellulizers {
  implicit def optionize[T](t: T): Option[T] = Option(t)
  implicit def nameColumn[F, T, C](args: (String, Column[F, T, C])) = NamedColumn(cellulize(args._1), args._2)
  implicit def nameColumn2[F, T, C](args: (String, Column[F, T, C]))(implicit m: Manifest[C]) = NamedColumn2(cellulize(args._1), args._2)
  class Blank[T](implicit val m: Manifest[T]) extends Cell[T] {
    val value = None
  }
  def blank[T](implicit m: Manifest[T]) = new Blank[T]
  implicit def aggregator2agg[F, T, C](aggregator: Aggregator[F, T, C]): (Column[F, T, C], Aggregator[F, T, C]) = aggregator.col -> aggregator

  type Column1From[F] = { type l[a] = Column1[a] { type From = F } }
  type Column1In[F] = Column1From[F]
  type Column1Out[F] = ({ type l[a] = F => Cell[a] })
  type Column1HRFn[F] = (Column1In[F]#l ~> Column1Out[F]#l)
}

import Tabula._

trait Cell[A] {
  def value: Option[A]
  def m: Manifest[A]
}

abstract class Column1[C] {
  type From
  def apply(x: From): Cell[C]
}

abstract class Column[F, T, C](val f: F => Option[T])(implicit val cz: Cellulizer[T, C]) extends (F => Cell[C]) {
  self =>

  def apply(x: F): Cell[C] = (f andThen cz.apply)(x)

  abstract class Transform[F, T, C1, TT, C2](
    val left: Column[F, T, C1],
    val right: Column[T, TT, C2]) extends Column[F, TT, C2](left.f(_).flatMap(right.f))(right.cz)

  def |[TT, CC](right: Column[T, TT, CC]) =
    new Transform[F, T, C, TT, CC](this, right) {}

  lazy val column1: Column1From[F]#l[C] = new Column1[C] {
    type From = F
    def apply(x: From): Cell[C] = self(x)
  }

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

case class NamedColumn2[F, T, C](name: Cell[String], column: Column[F, T, C]) extends Column[F, T, C](column.f)(column.cz) {
  def !:[TT, CC](next: NamedColumn2[F, TT, CC])(implicit mcc: Manifest[CC]) =
    next !: RowModel2[F, T, C, NamedColumn2[F, T, C], HNil, (F => Cell[C]), HNil](this :: HNil)
}

class Runner[F, T] extends (({ type l[a] = NamedColumn2[F, T, a] })#l ~> ({ type l[a] = F => Cell[a] })#l) with NoDefault
object Runner extends (Column1 ~> Cell) with NoDefault

case class RowModel2[F, T, C, InH <: NamedColumn2[F, T, C], InT <: HList, OutH <: (F => Cell[C]), OutT <: HList](columns: InH :: InT) {
  lazy val runner = new Runner[F, T]
  implicit val funny = runner.λ[C](_.apply)

  def !:[TT, CC](next: NamedColumn2[F, TT, CC])(implicit mcc: Manifest[CC]) =
    RowModel2[F, TT, CC, NamedColumn2[F, TT, CC], InH :: InT, (F => Cell[CC]), OutH :: OutT](next :: columns)
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
    cs.reduceLeft((a, b) => Tabula.cellulize(f(a.value, b.value)))
}

abstract class Fold[F, T, C](val col: Column[F, T, C])(init: => T)(f: (Option[C], Option[C]) => Option[T])(implicit cz: Cellulizer[T, C]) extends Aggregator[F, T, C] {
  import cz._
  def apply(cs: List[Cell[C]]): Cell[C] =
    cs.foldLeft[Cell[C]](init)((a, b) => f(a.value, b.value))
}
