package tabula.excel.test

import java.io.{ File, FileOutputStream }
import tabula._
import Tabula._
import tabula.excel._
import tabula.test._
import org.specs._
import scala.xml._
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.hssf.usermodel.HSSFWorkbook

abstract class MyExcelSheet(name: String)(implicit workbook: Workbook) extends ExcelSheet(name) {
  implicit object NodeSeqFormatter extends SimpleFormatter[NodeSeq] {
    def apply(cell: Cell[NodeSeq]) = implicitly[Formatter[String]].apply(cell.map(_.toString))
  }
}

class ExcelSpec extends Specification {
  import ShowcaseSpec._
  "a Excel output" should {
    "produce Excel output" in {
      val workbook = Excel(() => new HSSFWorkbook()) {
        implicit wb =>
          object sheet extends MyExcelSheet("excel spec")
          for (purchase <- Purchases.*)
            cellsF(purchase).row(sheet)
      }
      val file = File.createTempFile(getClass.getName+".", ".xls")
      val out = new FileOutputStream(file)
      workbook.write(out)
      out.flush()
      out.close()
      println(file)
    }
  }
}
