package tabula

import Tabula._

abstract class Aggregator[F, T, C] {
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
    cs.foldLeft[Cell[C]](cellulize(init))((a, b) => cellulize(f(a.value, b.value)))
}

trait Aggregators {
  implicit def aggregator2agg[F, T, C](aggregator: Aggregator[F, T, C]): (Column[F, T, C], Aggregator[F, T, C]) = aggregator.col -> aggregator
}
