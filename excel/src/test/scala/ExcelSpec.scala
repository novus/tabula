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
    def apply(value: Option[NodeSeq]) = implicitly[Formatter[String]].apply(value.map(_.toString))
  }
}

class ExcelSpec extends Specification {
  import ShowcaseSpec._
  "a Excel output" should {
    "produce Excel output" in {
      Excel(() => new HSSFWorkbook()) {
        implicit wb =>
          object sheet1 extends MyExcelSheet("excel spec - sheet one")
          object sheet2 extends MyExcelSheet("excel spec - sheet two")
          val file = File.createTempFile(getClass.getName+".", ".xls")
          val writer1 = sheet1.writer(columns).toFile(file)
          val writer2 = sheet2.writer(columns).toFile(file)
          writer1.write(for (purchase <- Purchases.*.iterator) yield cellsF(purchase).row(sheet1))
          writer2.write(for (purchase <- Purchases.*.iterator) yield cellsF(purchase).row(sheet2))
          println(file)
      }
      success
    }
  }
}
