package tabula

trait TableModel[F] {
  self =>

  def columns: List[Column[F]]
  def rows(xs: List[F]): List[Row] = xs.map(x => Row(columns.map(_.apply(Some(x)).getOrElse(Blank))))
  def table(xs: List[F]) = Table(name = "", rows = rows(xs))
}

object TableModel {
  def apply[F](cs: List[Column[F]]) = new TableModel[F] { val columns = cs }
}

trait TableModelWithHeader[F] extends TableModel[F] {
  private def header_? = columns.exists { case ColumnWithMeta(_, _, _) => true case _ => false }
  def header =
    if (header_?)
      Some(Row(columns.map {
        case ColumnWithMeta(_, name, label) => StringCell(label.getOrElse(name))
        case _                              => Blank
      }))
    else None

  override def table(xs: List[F]) = super.table(xs).copy(header = header)
}
