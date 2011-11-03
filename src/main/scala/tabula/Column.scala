package tabula

trait Column[F] {
  def apply: CellFun[F]
  def apply(x: F): Option[Cell] = apply(Some(x))
}

trait Columns[F] extends Column[F] {
  val columns: List[Column[Cell]]

  def first: CellFun[F]

  def apply: CellFun[F] = {
    case x =>
      columns.foldLeft(first(x)) {
        case (cell @ Some(_), col) => col(cell)
        case _                     => None
      }
  }
}

case class UpperCaseColumn(first: CellFun[StringCell], columns: List[Column[Cell]]) extends Columns[StringCell] {
}
