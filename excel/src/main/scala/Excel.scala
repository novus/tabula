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
    def apply(cell: Cell[String]) = createCell(ec => cell.value.foreach(ec.setCellValue))
  }

  implicit object DateTimeFormatter extends SimpleFormatter[DateTime] {
    val cellStyle = {
      val style = workbook.createCellStyle()
      style.setDataFormat(workbook.createDataFormat().getFormat("m/d/yy h:mm"))
      style
    }
    def apply(cell: Cell[DateTime]) =
      createCellAnd {
        ec =>
          ec.setCellStyle(cellStyle)
          cell.value.map(_.toDate).foreach(ec.setCellValue)
          ec
      }
  }

  implicit object DoubleFormatter extends SimpleFormatter[Double] {
    def apply(cell: Cell[Double]) =
      createCell(ec => cell.value.map(_.toDouble).foreach(ec.setCellValue))
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
      (fter(cell): Base)(lastCellIndex, row)
      (lastCellIndex + 1, row)
    }
  }

  def writer(names: List[Option[String]]) = new WriterSpawn(names) {
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
