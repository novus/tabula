package tabula

import org.apache.poi.hssf.usermodel.HSSFWorkbook

case class Table(name: String, header: Option[Row] = None, rows: List[Row], footer: Option[Row] = None)
