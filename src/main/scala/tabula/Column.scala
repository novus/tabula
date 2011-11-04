package tabula

trait Column[F] {
  def apply: CellFun[F]
}

case class Named[F](column: Column[F], name: String, label: Option[String] = None) extends Column[F] {
  def apply = column.apply
}

trait Columns[F, C <: Cell] extends Column[F] {
  val columns: ColumnsChain[C]

  def first: Column[F]

  def apply = {
    case x =>
      columns.foldLeft(first.apply(x)) {
        case (cell: Option[C], col) => col.apply(cell)
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
  def apply = column.apply
  val fun: AggregationFun[F, C]
}
