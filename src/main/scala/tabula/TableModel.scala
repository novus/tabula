package tabula

trait TableModel[F] {
  self =>

  def columns: List[Column[F]]
  def header: Option[Row]
  def rows(xs: List[F]): List[Row] =
    xs.map(Option(_)).map(x => Row(columns.flatMap(_.cellOrBlank(x))))
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

  private lazy val header_? = columns.exists { case Named(_, _, _) => true case _ => false }

  override lazy val header =
    if (header_?)
      Some(Row(columns.map {
        case Named(_, name, label) => StringCell(label.getOrElse(name))
        case _                     => Blank
      }))
    else None
}

trait TableModelWithFooter[F] {
  self: TableModel[F] =>

  private lazy val footer_? = columns.exists { case _: Aggregated[_, _] => true case _ => false }

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
