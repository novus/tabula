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

object MyExcel extends Excel {
  implicit object NodeSeqFormatter extends SimpleFormatter[NodeSeq] {
    def apply(value: Option[NodeSeq]) = implicitly[Formatter[String]].apply(value.map(_.toString))
  }
}

class ExcelSpec extends Specification {
  import ShowcaseSpec._
  "a Excel output" should {
    "produce Excel output" in {
      val file = File.createTempFile(getClass.getName+".", ".xls")
      val writer = MyExcel.writer(columns).toFile(file)
      writer.write(for (purchase <- Purchases.*.iterator) yield cellsF(purchase).row(MyExcel))
      println(file)
      success
    }
  }
}
