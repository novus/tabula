package tabula

trait TableModel[T] {
  def columns: List[Column[T]]
  def header(xs: List[T]): Option[Row]
  def rows(xs: List[T]): List[Row] = xs.map(x => Row(columns.map(_(x).getOrElse(Blank))))
  def footer(xs: List[T]): Option[Row]
}
