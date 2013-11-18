package tabula.excel

import shapeless._
import tabula._
import Tabula._
import org.apache.poi.ss.usermodel.{ Workbook, Sheet, Row => ExcelRow, Cell => ExcelCell, CellStyle }
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.joda.time._
import java.io.OutputStream
import scala.collection.mutable.ArrayBuffer

case class ExcelContext(workbook: Workbook) {
  object styles {
    lazy val date = {
      val style = workbook.createCellStyle()
      style.setDataFormat(workbook.createDataFormat().getFormat("m/d/yy h:mm"))
      style
    }
  }
}

sealed abstract class ICell extends ((ExcelContext, ExcelRow, Int) => ExcelCell) {
  protected def createCellAnd(ctx: ExcelContext, row: ExcelRow, idx: Int)(f: ExcelCell => Unit) = {
    val cell = row.createCell(idx)
    f(cell)
    cell
  }

  protected def createCell(ctx: ExcelContext, row: ExcelRow, idx: Int) =
    createCellAnd(ctx, row, idx)(_ => ())
}

object NilCell extends ICell {
  def apply(ctx: ExcelContext, row: ExcelRow, idx: Int) = createCell(ctx, row, idx)
}

case class StringCell(value: String) extends ICell {
  def apply(ctx: ExcelContext, row: ExcelRow, idx: Int) = createCellAnd(ctx, row, idx)(_.setCellValue(value))
}

case class DateTimeCell(value: DateTime) extends ICell {
  def apply(ctx: ExcelContext, row: ExcelRow, idx: Int) =
    createCellAnd(ctx, row, idx) {
      cell =>
        cell.setCellValue(value.toDate)
        cell.setCellStyle(ctx.styles.date)
    }
}

case class DoubleCell(value: Double) extends ICell {
  def apply(ctx: ExcelContext, row: ExcelRow, idx: Int) = createCellAnd(ctx, row, idx)(_.setCellValue(value))
}

class IRow(cells: ArrayBuffer[ICell]) extends ((ExcelContext, Sheet, Int) => ExcelRow) {
  def ++=(more: Iterable[ICell]) = cells ++= more
  def apply(ctx: ExcelContext, sheet: Sheet, idx: Int) = {
    val row = sheet.createRow(idx)
    for ((cell, cellIdx) <- cells.zipWithIndex) cell(ctx, row, cellIdx)
    row
  }
}

abstract class Excel extends Format {
  type Row = IRow
  type Base = ICell

  implicit object StringFormatter extends SimpleFormatter[String] {
    def apply(value: Option[String]) = value.map(StringCell(_)).getOrElse(NilCell) :: Nil
  }

  implicit object DateTimeFormatter extends SimpleFormatter[DateTime] {
    def apply(value: Option[DateTime]) = value.map(DateTimeCell(_)).getOrElse(NilCell) :: Nil
  }

  implicit object DoubleFormatter extends SimpleFormatter[Double] {
    def apply(value: Option[Double]) = value.map(DoubleCell(_)).getOrElse(NilCell) :: Nil
  }

  object RowProto extends RowProto {
    def emptyRow = new IRow(new ArrayBuffer)
    def appendCell[C](cell: CellT[C])(row: Row)(implicit fter: Formatter[C]) = {
      row ++= fter(cell)
      row
    }
  }

  class Spawn(names: List[Option[String]]) extends WriterSpawn(names) {
    /* There's a better way to stream an *SSF workbook to disk:
     http://poi.apache.org/spreadsheet/how-to.html#sxssf */
    def toStream(out: OutputStream) = new Writer(out) {
      def writeMore(rows: Iterator[Row]) = throw new UnsupportedOperationException("not implemented")
      override def write(rows: Iterator[Row]) {
        start()
        Excel(() => new HSSFWorkbook()) {
          workbook =>
            val ctx = ExcelContext(workbook)
            val sheet = workbook.createSheet()
            for ((row, rowIdx) <- (Iterator.single(RowProto.header(names)) ++ rows).zipWithIndex)
              row(ctx, sheet, rowIdx)
            workbook.write(out)
        }
        finish()
      }
      override def finish() = out.flush()
    }
  }

  def writer(names: List[Option[String]]) = new Spawn(names)
}

object Excel {
  def apply[WB <: Workbook](init: () => WB)(f: WB => Unit): WB = {
    val book = init()
    f(book)
    book
  }
}
