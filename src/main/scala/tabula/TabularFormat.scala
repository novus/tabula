package tabula

import org.apache.poi.hssf.usermodel._

trait TabularFormat

trait AsCSV extends TabularFormat {
  def asCSV: String
}

trait AsXLS extends TabularFormat {
  def asBytes(autoSize: Boolean = false) = {
    val out = new java.io.ByteArrayOutputStream
    asXLS(autoSize).write(out)
    out.toByteArray
  }

  def asXLS: HSSFWorkbook

  def asXLS(autoSize: Boolean): HSSFWorkbook
}
