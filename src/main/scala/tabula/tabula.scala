package tabula

import java.text.DecimalFormat

object `package` {
  val Blank = StringCell(None)
  val SomeBlank = Some(Blank)

  type CellFun[F] = PartialFunction[Option[F], Option[Cell]]
  type AggregationFun[F, C <: Cell] = List[(F, Cell)] => Option[C]
  type ColumnsChain[C] = List[Column[C]]

  implicit def wtf(c: Column[_]): Column[Cell] = c.asInstanceOf[Column[Cell]]

  implicit def colpimp[F](col: Column[F]) = new {
    def |[N <: Cell](next: Column[N]): Columns[F, N] = col match {
      case chain: Columns[F, N] => Columns(chain.first, chain.columns ::: next :: Nil)
      case _                    => Columns(col, next :: Nil)
    }
  }
}
