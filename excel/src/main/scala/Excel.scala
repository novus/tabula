package tabula.excel

import shapeless._
import tabula._
import Tabula._
import org.apache.poi.ss.usermodel.{ Workbook, Sheet, Row => ExcelRow, Cell => ExcelCell, CellStyle }
import org.joda.time._
import java.io.OutputStream

class ExcelSheet(name: String)(implicit protected val workbook: Workbook) extends Format {
  type Row = (Int, ExcelRow)
  type Base = Row => ExcelCell

  lazy val sheet = workbook.createSheet(name)

  def createCellAnd(f: ExcelCell => ExcelCell): Base = {
    case (idx, row) =>
      f(row.createCell(idx))
  }

  def createCell(f: ExcelCell => Unit): Base =
    createCellAnd {
      ec =>
        f(ec)
        ec
    }

  implicit object StringFormatter extends SimpleFormatter[String] {
    def apply(value: Option[String]) = createCell(ec => value.foreach(ec.setCellValue)) :: Nil
  }

  implicit object DateTimeFormatter extends SimpleFormatter[DateTime] {
    val cellStyle = {
      val style = workbook.createCellStyle()
      style.setDataFormat(workbook.createDataFormat().getFormat("m/d/yy h:mm"))
      style
    }
    def apply(value: Option[DateTime]) =
      createCellAnd {
        ec =>
          ec.setCellStyle(cellStyle)
          value.map(_.toDate).foreach(ec.setCellValue)
          ec
      } :: Nil
  }

  implicit object DoubleFormatter extends SimpleFormatter[Double] {
    def apply(value: Option[Double]) =
      createCell(ec => value.map(_.toDouble).foreach(ec.setCellValue)) :: Nil
  }

  object RowProto extends RowProto {
    private var rowIndex = 0
    def emptyRow = {
      val row = sheet.createRow(rowIndex)
      rowIndex += 1
      (0, row)
    }
    def appendCell[C](cell: CellT[C])(row0: Row)(implicit fter: Formatter[C]) = {
      val (lastCellIndex, row) = row0
      val formatted: List[(Row => ExcelCell, Int)] = fter(cell).zipWithIndex
      for ((elem, idx) <- formatted) elem((lastCellIndex + idx) -> row)
      (lastCellIndex + formatted.size, row)
    }
  }

  def writer(names: List[Option[String]]) = new WriterSpawn(names) {
    /* There's a better way to stream an *SSF workbook to disk:
     http://poi.apache.org/spreadsheet/how-to.html#sxssf */
    def toStream(out: OutputStream) = new Writer(out) {
      override def start() = RowProto.header(names)
      def writeMore(rows: Iterator[Row]) {
        rows.foreach(identity)
        workbook.write(out)
      }
      override def finish() = out.flush()
    }
  }
}

object Excel {
  def apply[WB <: Workbook](init: () => WB)(f: WB => Unit): WB = {
    val book = init()
    f(book)
    book
  }
}
