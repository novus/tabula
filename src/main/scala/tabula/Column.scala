package tabula

trait Column[F] {
  def apply: CellFun[F]
}

trait Columns[F] extends Column[F] {
  val columns: List[Column[Cell]]

  def first: CellFun[F]

  def apply: CellFun[F] = {
    case x =>
      columns.foldLeft(first(x)) {
        case (cell @ Some(_), col) => col.apply(cell)
        case _                     => None
      }
  }
}
