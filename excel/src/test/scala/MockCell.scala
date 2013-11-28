package tabula.excel.test

import org.apache.poi.ss.usermodel.{ Cell, CellStyle }

class MockCell(underlying: Cell) extends Cell {
  private var _style = Option.empty[CellStyle]
  def style() = _style

  private var _value = Option.empty[Any]
  def value() = _value

  def setCellStyle(x$1: CellStyle): Unit = {
    _style = Some(x$1)
    underlying.setCellStyle(x$1)
  }

  def setCellValue(x$1: Boolean): Unit = {
    _value = Some(x$1)
    underlying.setCellValue(x$1)
  }

  def setCellValue(x$1: String): Unit = {
    _value = Some(x$1)
    underlying.setCellValue(x$1)
  }

  def setCellValue(x$1: org.apache.poi.ss.usermodel.RichTextString): Unit = {
    _value = Some(x$1)
    underlying.setCellValue(x$1)
  }

  def setCellValue(x$1: java.util.Calendar): Unit = {
    _value = Some(x$1)
    underlying.setCellValue(x$1)
  }

  def setCellValue(x$1: java.util.Date): Unit = {
    _value = Some(x$1)
    underlying.setCellValue(x$1)
  }

  def setCellValue(x$1: Double): Unit = {
    _value = Some(x$1)
    underlying.setCellValue(x$1)
  }

  def getArrayFormulaRange(): org.apache.poi.ss.util.CellRangeAddress = ???
  def getBooleanCellValue(): Boolean = ???
  def getCachedFormulaResultType(): Int = ???
  def getCellComment(): org.apache.poi.ss.usermodel.Comment = ???
  def getCellFormula(): String = ???
  def getCellStyle(): org.apache.poi.ss.usermodel.CellStyle = ???
  def getCellType(): Int = ???
  def getColumnIndex(): Int = ???
  def getDateCellValue(): java.util.Date = ???
  def getErrorCellValue(): Byte = ???
  def getHyperlink(): org.apache.poi.ss.usermodel.Hyperlink = ???
  def getNumericCellValue(): Double = ???
  def getRichStringCellValue(): org.apache.poi.ss.usermodel.RichTextString = ???
  def getRow(): org.apache.poi.ss.usermodel.Row = ???
  def getRowIndex(): Int = ???
  def getSheet(): org.apache.poi.ss.usermodel.Sheet = ???
  def getStringCellValue(): String = ???
  def isPartOfArrayFormulaGroup(): Boolean = ???
  def removeCellComment(): Unit = ???
  def setAsActiveCell(): Unit = ???
  def setCellComment(x$1: org.apache.poi.ss.usermodel.Comment): Unit = ???
  def setCellErrorValue(x$1: Byte): Unit = ???
  def setCellFormula(x$1: String): Unit = ???
  def setCellType(x$1: Int): Unit = ???
  def setHyperlink(x$1: org.apache.poi.ss.usermodel.Hyperlink): Unit = ???
}
