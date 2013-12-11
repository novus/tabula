package tabula.excel.test

import org.apache.poi.ss.usermodel.{ Workbook, Sheet }
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import scala.collection.mutable.ArrayBuffer

class MockWorkbook(underlying: Workbook)(implicit me: MockExcel) extends Workbook {
  me.register(this)

  private val _sheets = ArrayBuffer.empty[MockSheet]
  def sheets() = _sheets.toList

  def createSheet(): Sheet = {
    val sheet = new MockSheet(underlying.createSheet())
    _sheets += sheet
    sheet
  }

  def createSheet(x$1: String): Sheet = {
    val sheet = new MockSheet(underlying.createSheet(x$1))
    _sheets += sheet
    sheet
  }

  def write(x$1: java.io.OutputStream): Unit = underlying.write(x$1)

  // things we proxy from underlying because they're difficult (or
  // impossible) to mock
  def createCellStyle(): org.apache.poi.ss.usermodel.CellStyle = underlying.createCellStyle()
  def createDataFormat(): org.apache.poi.ss.usermodel.DataFormat = underlying.createDataFormat()

  // things that can error out harmlessly
  def addPicture(x$1: Array[Byte], x$2: Int): Int = ???
  def addToolPack(x$1: org.apache.poi.ss.formula.udf.UDFFinder): Unit = ???
  def cloneSheet(x$1: Int): org.apache.poi.ss.usermodel.Sheet = ???
  def createFont(): org.apache.poi.ss.usermodel.Font = ???
  def createName(): org.apache.poi.ss.usermodel.Name = ???
  def findFont(x$1: Short, x$2: Short, x$3: Short, x$4: String, x$5: Boolean, x$6: Boolean, x$7: Short, x$8: Byte): org.apache.poi.ss.usermodel.Font = ???
  def getActiveSheetIndex(): Int = ???
  def getAllPictures(): java.util.List[_ <: org.apache.poi.ss.usermodel.PictureData] = ???
  def getCellStyleAt(x$1: Short): org.apache.poi.ss.usermodel.CellStyle = ???
  def getCreationHelper(): org.apache.poi.ss.usermodel.CreationHelper = ???
  def getFirstVisibleTab(): Int = ???
  def getFontAt(x$1: Short): org.apache.poi.ss.usermodel.Font = ???
  def getForceFormulaRecalculation(): Boolean = ???
  def getMissingCellPolicy(): org.apache.poi.ss.usermodel.Row.MissingCellPolicy = ???
  def getName(x$1: String): org.apache.poi.ss.usermodel.Name = ???
  def getNameAt(x$1: Int): org.apache.poi.ss.usermodel.Name = ???
  def getNameIndex(x$1: String): Int = ???
  def getNumCellStyles(): Short = ???
  def getNumberOfFonts(): Short = ???
  def getNumberOfNames(): Int = ???
  def getNumberOfSheets(): Int = ???
  def getPrintArea(x$1: Int): String = ???
  def getSheet(x$1: String): org.apache.poi.ss.usermodel.Sheet = ???
  def getSheetAt(x$1: Int): org.apache.poi.ss.usermodel.Sheet = ???
  def getSheetIndex(x$1: org.apache.poi.ss.usermodel.Sheet): Int = ???
  def getSheetIndex(x$1: String): Int = ???
  def getSheetName(x$1: Int): String = ???
  def isHidden(): Boolean = ???
  def isSheetHidden(x$1: Int): Boolean = ???
  def isSheetVeryHidden(x$1: Int): Boolean = ???
  def removeName(x$1: String): Unit = ???
  def removeName(x$1: Int): Unit = ???
  def removePrintArea(x$1: Int): Unit = ???
  def removeSheetAt(x$1: Int): Unit = ???
  def setActiveSheet(x$1: Int): Unit = ???
  def setFirstVisibleTab(x$1: Int): Unit = ???
  def setForceFormulaRecalculation(x$1: Boolean): Unit = ???
  def setHidden(x$1: Boolean): Unit = ???
  def setMissingCellPolicy(x$1: org.apache.poi.ss.usermodel.Row.MissingCellPolicy): Unit = ???
  def setPrintArea(x$1: Int, x$2: Int, x$3: Int, x$4: Int, x$5: Int): Unit = ???
  def setPrintArea(x$1: Int, x$2: String): Unit = ???
  def setRepeatingRowsAndColumns(x$1: Int, x$2: Int, x$3: Int, x$4: Int, x$5: Int): Unit = ???
  def setSelectedTab(x$1: Int): Unit = ???
  def setSheetHidden(x$1: Int, x$2: Int): Unit = ???
  def setSheetHidden(x$1: Int, x$2: Boolean): Unit = ???
  def setSheetName(x$1: Int, x$2: String): Unit = ???
  def setSheetOrder(x$1: String, x$2: Int): Unit = ???
}
