package tabula.excel.test

import org.apache.poi.ss.usermodel.Sheet
import scala.collection.mutable.ArrayBuffer

class MockSheet(underlying: Sheet) extends Sheet {
  private val _rows = ArrayBuffer.empty[MockRow]
  def rows() = _rows.toList

  def createRow(x$1: Int): org.apache.poi.ss.usermodel.Row = {
    val row = new MockRow(underlying.createRow(x$1))
    _rows += row
    row
  }

  // Members declared in java.lang.Iterable
  def iterator(): java.util.Iterator[org.apache.poi.ss.usermodel.Row] = ???
  // Members declared in org.apache.poi.ss.usermodel.Sheet
  def addMergedRegion(x$1: org.apache.poi.ss.util.CellRangeAddress): Int = ???
  def addValidationData(x$1: org.apache.poi.ss.usermodel.DataValidation): Unit = ???
  def autoSizeColumn(x$1: Int, x$2: Boolean): Unit = ???
  def autoSizeColumn(x$1: Int): Unit = ???
  def createDrawingPatriarch(): org.apache.poi.ss.usermodel.Drawing = ???
  def createFreezePane(x$1: Int, x$2: Int): Unit = ???
  def createFreezePane(x$1: Int, x$2: Int, x$3: Int, x$4: Int): Unit = ???
  def createSplitPane(x$1: Int, x$2: Int, x$3: Int, x$4: Int, x$5: Int): Unit = ???
  def getAutobreaks(): Boolean = ???
  def getCellComment(x$1: Int, x$2: Int): org.apache.poi.ss.usermodel.Comment = ???
  def getColumnBreaks(): Array[Int] = ???
  def getColumnStyle(x$1: Int): org.apache.poi.ss.usermodel.CellStyle = ???
  def getColumnWidth(x$1: Int): Int = ???
  def getDataValidationHelper(): org.apache.poi.ss.usermodel.DataValidationHelper = ???
  def getDefaultColumnWidth(): Int = ???
  def getDefaultRowHeight(): Short = ???
  def getDefaultRowHeightInPoints(): Float = ???
  def getDisplayGuts(): Boolean = ???
  def getFirstRowNum(): Int = ???
  def getFitToPage(): Boolean = ???
  def getFooter(): org.apache.poi.ss.usermodel.Footer = ???
  def getForceFormulaRecalculation(): Boolean = ???
  def getHeader(): org.apache.poi.ss.usermodel.Header = ???
  def getHorizontallyCenter(): Boolean = ???
  def getLastRowNum(): Int = ???
  def getLeftCol(): Short = ???
  def getMargin(x$1: Short): Double = ???
  def getMergedRegion(x$1: Int): org.apache.poi.ss.util.CellRangeAddress = ???
  def getNumMergedRegions(): Int = ???
  def getPaneInformation(): org.apache.poi.hssf.util.PaneInformation = ???
  def getPhysicalNumberOfRows(): Int = ???
  def getPrintSetup(): org.apache.poi.ss.usermodel.PrintSetup = ???
  def getProtect(): Boolean = ???
  def getRepeatingColumns(): org.apache.poi.ss.util.CellRangeAddress = ???
  def getRepeatingRows(): org.apache.poi.ss.util.CellRangeAddress = ???
  def getRow(x$1: Int): org.apache.poi.ss.usermodel.Row = ???
  def getRowBreaks(): Array[Int] = ???
  def getRowSumsBelow(): Boolean = ???
  def getRowSumsRight(): Boolean = ???
  def getScenarioProtect(): Boolean = ???
  def getSheetConditionalFormatting(): org.apache.poi.ss.usermodel.SheetConditionalFormatting = ???
  def getSheetName(): String = ???
  def getTopRow(): Short = ???
  def getVerticallyCenter(): Boolean = ???
  def getWorkbook(): org.apache.poi.ss.usermodel.Workbook = ???
  def groupColumn(x$1: Int, x$2: Int): Unit = ???
  def groupRow(x$1: Int, x$2: Int): Unit = ???
  def isColumnBroken(x$1: Int): Boolean = ???
  def isColumnHidden(x$1: Int): Boolean = ???
  def isDisplayFormulas(): Boolean = ???
  def isDisplayGridlines(): Boolean = ???
  def isDisplayRowColHeadings(): Boolean = ???
  def isDisplayZeros(): Boolean = ???
  def isPrintGridlines(): Boolean = ???
  def isRightToLeft(): Boolean = ???
  def isRowBroken(x$1: Int): Boolean = ???
  def isSelected(): Boolean = ???
  def protectSheet(x$1: String): Unit = ???
  def removeArrayFormula(x$1: org.apache.poi.ss.usermodel.Cell): org.apache.poi.ss.usermodel.CellRange[_ <: org.apache.poi.ss.usermodel.Cell] = ???
  def removeColumnBreak(x$1: Int): Unit = ???
  def removeMergedRegion(x$1: Int): Unit = ???
  def removeRow(x$1: org.apache.poi.ss.usermodel.Row): Unit = ???
  def removeRowBreak(x$1: Int): Unit = ???
  def rowIterator(): java.util.Iterator[org.apache.poi.ss.usermodel.Row] = ???
  def setArrayFormula(x$1: String, x$2: org.apache.poi.ss.util.CellRangeAddress): org.apache.poi.ss.usermodel.CellRange[_ <: org.apache.poi.ss.usermodel.Cell] = ???
  def setAutoFilter(x$1: org.apache.poi.ss.util.CellRangeAddress): org.apache.poi.ss.usermodel.AutoFilter = ???
  def setAutobreaks(x$1: Boolean): Unit = ???
  def setColumnBreak(x$1: Int): Unit = ???
  def setColumnGroupCollapsed(x$1: Int, x$2: Boolean): Unit = ???
  def setColumnHidden(x$1: Int, x$2: Boolean): Unit = ???
  def setColumnWidth(x$1: Int, x$2: Int): Unit = ???
  def setDefaultColumnStyle(x$1: Int, x$2: org.apache.poi.ss.usermodel.CellStyle): Unit = ???
  def setDefaultColumnWidth(x$1: Int): Unit = ???
  def setDefaultRowHeight(x$1: Short): Unit = ???
  def setDefaultRowHeightInPoints(x$1: Float): Unit = ???
  def setDisplayFormulas(x$1: Boolean): Unit = ???
  def setDisplayGridlines(x$1: Boolean): Unit = ???
  def setDisplayGuts(x$1: Boolean): Unit = ???
  def setDisplayRowColHeadings(x$1: Boolean): Unit = ???
  def setDisplayZeros(x$1: Boolean): Unit = ???
  def setFitToPage(x$1: Boolean): Unit = ???
  def setForceFormulaRecalculation(x$1: Boolean): Unit = ???
  def setHorizontallyCenter(x$1: Boolean): Unit = ???
  def setMargin(x$1: Short, x$2: Double): Unit = ???
  def setPrintGridlines(x$1: Boolean): Unit = ???
  def setRepeatingColumns(x$1: org.apache.poi.ss.util.CellRangeAddress): Unit = ???
  def setRepeatingRows(x$1: org.apache.poi.ss.util.CellRangeAddress): Unit = ???
  def setRightToLeft(x$1: Boolean): Unit = ???
  def setRowBreak(x$1: Int): Unit = ???
  def setRowGroupCollapsed(x$1: Int, x$2: Boolean): Unit = ???
  def setRowSumsBelow(x$1: Boolean): Unit = ???
  def setRowSumsRight(x$1: Boolean): Unit = ???
  def setSelected(x$1: Boolean): Unit = ???
  def setVerticallyCenter(x$1: Boolean): Unit = ???
  def setZoom(x$1: Int, x$2: Int): Unit = ???
  def shiftRows(x$1: Int, x$2: Int, x$3: Int, x$4: Boolean, x$5: Boolean): Unit = ???
  def shiftRows(x$1: Int, x$2: Int, x$3: Int): Unit = ???
  def showInPane(x$1: Short, x$2: Short): Unit = ???
  def ungroupColumn(x$1: Int, x$2: Int): Unit = ???
  def ungroupRow(x$1: Int, x$2: Int): Unit = ???
}
