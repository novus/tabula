package tabula

trait Column[F] {
  def apply(x: Option[F]): Option[Cell]
  def apply(x: F): Option[Cell] = apply(Some(x))
}

trait Columns[F] extends Column[F] {
  val columns: List[Column[Cell]]
  def first(x: Option[F]): Option[Cell]
  def last(x: Option[Cell]): Option[Cell] = x
  def apply(x: Option[F]): Option[Cell] = {
    last(columns.foldLeft(first(x)) { case (cell @ Some(_), col) => col(cell) case _ => None })
  }
}
