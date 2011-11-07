package tabula

trait TableModel[F] {
  self =>

  def columns: List[Column[F]]
  def header: Option[Row]
  def rows(xs: List[F]): List[Row] = xs.map(x => Row(columns.map(_.apply(Some(x)).getOrElse(Blank))))
  def footer(xs: List[F], rows: List[Row]): Option[Row]
  def table(xs: List[F]) = {
    val table = Table(name = "", header = header, rows = rows(xs))
    table.copy(footer = footer(xs, table.rows))
  }
}

object TableModel {
  def apply[F](cs: List[Column[F]]) =
    new TableModel[F] with TableModelWithHeader[F] with TableModelWithFooter[F] { val columns = cs }
}

trait TableModelWithHeader[F] {
  self: TableModel[F] =>

  private def header_? = columns.exists { case Named(_, _, _) => true case _ => false }

  override def header =
    if (header_?)
      Some(Row(columns.map {
        case Named(_, name, label) => StringCell(label.getOrElse(name))
        case _                     => Blank
      }))
    else None
}

trait TableModelWithFooter[F] {
  self: TableModel[F] =>

  private def footer_? = columns.exists { case _: Aggregated[_, _] => true case _ => false }

  override def footer(xs: List[F], rows: List[Row]) =
    if (footer_?)
      Some(Row(columns.zipWithIndex.map {
        case (agg: Aggregated[F, Cell], idx) => {
          agg.fun(xs.zip(rows.flatMap(_.cells.lift(idx)))).getOrElse(Blank)
        }
        case _ => Blank
      }))
    else None
}
