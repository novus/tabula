package tabula.excel.test

import scala.collection.mutable.ArrayBuffer

class MockExcel {
  private val _workbooks = ArrayBuffer.empty[MockWorkbook]
  def workbooks() = _workbooks.toList

  def register(workbook: MockWorkbook) {
    _workbooks += workbook
  }
}
