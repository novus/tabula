package tabula.excel.test

import java.io.{ File, FileOutputStream }
import tabula._
import Tabula._
import tabula.excel._
import tabula.test._
import org.specs2.mutable._
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
      Excel(() => new HSSFWorkbook()) {
        implicit wb =>
          object sheet extends MyExcelSheet("excel spec")
          val file = File.createTempFile(getClass.getName+".", ".xls")
          val writer = sheet.writer(columns).toFile(file)
          writer.write(for (purchase <- Purchases.*.iterator) yield cellsF(purchase).row(sheet))
          println(file)
      }
      success
    }
  }
}
