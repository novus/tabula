package tabula

import org.apache.poi.hssf.usermodel.HSSFWorkbook

case class Table(name: String, header: Option[Row] = None, rows: List[Row], footer: Option[Row] = None) extends AsCSV with AsXLS {
  lazy val asCSV = (header.toList ::: rows ::: footer.toList).map(_.asCSV).mkString("\n")
  lazy val asXLS: HSSFWorkbook = asXLS(false)
  def asXLS(autoSize: Boolean = false) =
    TableSet(name = name, tables = List(this)).asXLS(autoSize)
}
