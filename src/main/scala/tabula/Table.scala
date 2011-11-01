/**
 */
package tabula

import com.mongodb.casbah.Imports._
import org.apache.poi.hssf.usermodel.HSSFWorkbook

case class Table(name: String, header: Row, rows: List[Row]) extends AsCSV with AsXLS {
  lazy val asCSV = (header :: rows ::: Nil).map(_.asCSV).mkString("\n")
  lazy val asXLS: HSSFWorkbook = asXLS(false)
  def asXLS(autoSize: Boolean = false) =
    TableSet(name = name, tables = List(this)).asXLS(autoSize)
}
