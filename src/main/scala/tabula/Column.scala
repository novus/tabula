package tabula

trait Column[F] {
  def cell: CellFun[F]
}

case class Named[F](column: Column[F], name: String, label: Option[String] = None) extends Column[F] {
  def cell = column.cell
}

trait Columns[F, C <: Cell] extends Column[F] {
  val columns: ColumnsChain[C]

  def first: Column[F]

  def cell = {
    case x =>
      columns.foldLeft(first.cell(x)) {
        case (cell: Option[C], col) => col.cell(cell)
      }
  }
}

object Columns {
  def apply[F, C <: Cell](f: Column[F], cs: ColumnsChain[C]) =
    new Columns[F, C] {
      val first = f
      val columns = cs
    }
}

trait Aggregated[F, C <: Cell] extends Column[F] {
  val column: Column[F]
  def cell = column.cell
  def fun: AggregationFun[F, C]
}

object Aggregated {
  def apply[F, C <: Cell](c: Column[F])(f: AggregationFun[F, C]) =
    new Aggregated[F, C] {
      val column = c
      val fun = f
    }
}
