package tabula.excel.test

import java.io.{ File, FileOutputStream }
import tabula._
import Tabula._
import tabula.excel._
import tabula.test._
import tabula.test.Extensibility._
import shapeless._
import org.specs2.mutable._
import scala.xml._
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.hssf.usermodel.HSSFWorkbook

object MyExcel extends Excel {
  implicit object NodeSeqFormatter extends SimpleFormatter[NodeSeq] {
    def apply(value: Option[NodeSeq]) = implicitly[Formatter[String]].apply(value.map(_.toString))
  }
}

object Sheet1 {
  val columns =
    (ItemName | Capitalize) @@ "Item Name" ::
      Title ::
      ItemPrice @@ "Item Price" ::
      PurchaseLocation @@ "Bought At" ::
      DateOfPurchase @@ "Date of Purchase" ::
      HNil

  val cellsF = cells(columns)
}

object Sheet2 {
  val columns =
    (ItemName | Capitalize) @@ "Item Name" ::
      Tags ::
      HNil

  val cellsF = cells(columns)
}

class ExcelSpec extends Specification {
  import ShowcaseSpec._
  "a Excel output" should {
    "produce Excel output" in {
      implicit object me extends MockExcel
      val file = File.createTempFile(getClass.getName+".", ".xls")
      val out = new FileOutputStream(file)
      Excel(() => new MockWorkbook(new HSSFWorkbook())) {
        ctx =>
          val writer1 = MyExcel.writer(Sheet1.columns).toWorkbook(ctx)
          val writer2 = MyExcel.writer(Sheet2.columns).toWorkbook(ctx)
          writer1.start()
          for (purchase <- Purchases.*) {
            val row = Sheet1.cellsF(purchase).row(MyExcel)
            writer1.writeMore(Iterator.single(row))
          }
          writer1.finish()
          writer2.write(for (purchase <- Purchases.*.iterator) yield Sheet2.cellsF(purchase).row(MyExcel))
      }.workbook.write(out)
      out.flush()
      out.close()
      println(file)
      success
    }
  }
}
