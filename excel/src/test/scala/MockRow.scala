package tabula.excel.test

import org.apache.poi.ss.usermodel.Row
import scala.collection.mutable.ArrayBuffer

class MockRow(underlying: Row) extends Row {
  private val _cells = ArrayBuffer.empty[MockCell]
  def cells() = _cells.toList

  def createCell(x$1: Int): org.apache.poi.ss.usermodel.Cell = {
    val cell = new MockCell(underlying.createCell(x$1))
    _cells += cell
    cell
  }

  // Members declared in java.lang.Iterable
  def iterator(): java.util.Iterator[org.apache.poi.ss.usermodel.Cell] = ???
  // Members declared in org.apache.poi.ss.usermodel.Row
  def cellIterator(): java.util.Iterator[org.apache.poi.ss.usermodel.Cell] = ???
  def createCell(x$1: Int, x$2: Int): org.apache.poi.ss.usermodel.Cell = ???
  def getCell(x$1: Int, x$2: org.apache.poi.ss.usermodel.Row.MissingCellPolicy): org.apache.poi.ss.usermodel.Cell = ???
  def getCell(x$1: Int): org.apache.poi.ss.usermodel.Cell = ???
  def getFirstCellNum(): Short = ???
  def getHeight(): Short = ???
  def getHeightInPoints(): Float = ???
  def getLastCellNum(): Short = ???
  def getPhysicalNumberOfCells(): Int = ???
  def getRowNum(): Int = ???
  def getRowStyle(): org.apache.poi.ss.usermodel.CellStyle = ???
  def getSheet(): org.apache.poi.ss.usermodel.Sheet = ???
  def getZeroHeight(): Boolean = ???
  def isFormatted(): Boolean = ???
  def removeCell(x$1: org.apache.poi.ss.usermodel.Cell): Unit = ???
  def setHeight(x$1: Short): Unit = ???
  def setHeightInPoints(x$1: Float): Unit = ???
  def setRowNum(x$1: Int): Unit = ???
  def setRowStyle(x$1: org.apache.poi.ss.usermodel.CellStyle): Unit = ???
  def setZeroHeight(x$1: Boolean): Unit = ???
}
