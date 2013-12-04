package tabula

import shapeless._
import Tabula._

abstract class Columns[F, T, C, Tail <: HList, O <: HList](val columns: Column[F, T, C] :: Tail)(
    implicit tl: ToList[Column[F, T, C] :: Tail, Column[_, _, _]],
    aa: ApplyAll[F, Column[F, T, C] :: Tail, ColumnAndCell[F, T, C] :: O]) {
  private val cellsF = cells(columns)
  def write[Fmt <: Format, W](fmt: Fmt)(f: fmt.Spawn => fmt.Writer[W])(xs: Iterator[F])(
    implicit lf: LeftFolder[ColumnAndCell[F, T, C] :: O, fmt.Row, Fmt]) =
    f(fmt.writer(columns)).write(xs.map(cellsF(_).row(fmt)))
}
