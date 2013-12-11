package tabula.excel.test

import java.io.{ File, FileOutputStream }
import tabula._
import Tabula._
import tabula.excel._
import tabula.test._
import tabula.test.Extensibility._
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
  import shapeless._

  object columns extends Columns(
    (ItemName | Capitalize) @@ "Item Name" ::
      Title ::
      ItemPrice @@ "Item Price" ::
      PurchaseLocation @@ "Bought At" ::
      DateOfPurchase @@ "Date of Purchase" ::
      HNil)

  val cellsF = cells(columns.columns)
}

object Sheet2 {
  import shapeless._

  object columns extends Columns(
    (ItemName | Capitalize) @@ "Item Name" ::
      Tags ::
      HNil)
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
          val writer1 = MyExcel.writer(Sheet1.columns.columns).toWorkbook(ctx, name = Some("sheet one"))
          writer1.start()
          for (purchase <- Purchases.*) {
            val row = Sheet1.cellsF(purchase).row(MyExcel)
            writer1.writeMore(Iterator.single(row))
          }
          writer1.finish()
          Sheet2.columns.write(MyExcel)(_.toWorkbook(ctx, name = Some("sheet two")))(Purchases.*.iterator)
      }.workbook.write(out)
      out.flush()
      out.close()
      println(file)

      val workbook :: Nil = me.workbooks()
      val sheet1 :: sheet2 :: Nil = workbook.sheets()
      sheet1.rows().size must_== 4
      sheet2.rows().size must_== 4

      val header1 :: rows1 = sheet1.rows()
      Purchases.*.zip(rows1).foreach {
        case (Purchase(UselessItem(_name, _price, _), _date, PretentiousPurveyor(_, _location)), row) =>
          val name :: _ :: price :: location :: date :: Nil = row.cells()
          name.value() must beSome.which { case x: String => x.toLowerCase == _name }
          price.value() must_== Some(_price)
          location.value() must_== Some(_location)
          date.value() must_== _date.map(_.toDate)
          _date match {
            case Some(_) => date.style() must beSome
            case _       => date.style() must beNone
          }
      }

      sheet2.rows().forall(_.cells().forall(_.style().isEmpty)) must beTrue

      val header2 :: rows2 = sheet2.rows()
      Purchases.*.zip(rows2).foreach {
        case (Purchase(UselessItem(_name, _, tags), _, _), row) =>
          val name :: tag_foo :: tag_bar :: tag_baz :: tag_quux :: Nil = row.cells()
          name.value() must beSome.which { case x: String => x.toLowerCase == _name }
          tag_foo.value().filterNot(_ == "") must_== tags.get("foo")
          tag_bar.value().filterNot(_ == "") must_== tags.get("bar")
          tag_baz.value().filterNot(_ == "") must_== tags.get("baz")
          tag_quux.value().filterNot(_ == "") must_== tags.get("quux")
      }

      success
    }
  }
}
