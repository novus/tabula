package tabula

trait TableModel[T] {
  def columns: List[Column[T]]
  def header: Option[Row]
  def rows(xs: List[T]): List[Row] = xs.map(x => Row(columns.map(_.apply(Some(x)).getOrElse(Blank))))
  def footer: Option[Row]
  def table(xs: List[T]) = Table(name = "", header = header.getOrElse(Row(Nil)), rows = rows(xs))
}

object TableModel {
  def apply[F](cs: List[Column[F]]) = new TableModel[F] {
    val columns = cs
    val header = None
    val footer = None
  }
}

trait TableModelHeader[T] {
  self: TableModel[T] =>

  def header = Row(columns.map {
    case ColumnWithMeta(_, name, label) => StringCell(label.getOrElse(name))
    case _                              => Blank
  })
}
