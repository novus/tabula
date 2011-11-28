package tabula

import org.apache.poi.hssf.usermodel._

trait Output[O] {
  type Input
  def apply(ts: Input): O
}

object CSV extends Output[String] {
  type Input = Table
  def apply(table: Table) =
    table match {
      case Table(_, header, rows, footer) =>
        (header.toList ::: rows ::: footer.toList).map(
          _.cells.map(_.format).mkString(",")).mkString("\n")
    }
}

trait XLS extends Output[HSSFWorkbook] {
  type Input = AsTableSet
  def autoSize: Boolean
  def apply(ts: AsTableSet) = {
    val workbook = new HSSFWorkbook
    val DateTimeCellStyle = {
      val style = workbook.createCellStyle
      style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"))
      style
    }
    ts.tables.foreach {
      case Table(table_name, header, rows, footer) => {
        val sheet = workbook.createSheet(table_name)
        (header.toList ::: rows.toList ::: footer.toList).zipWithIndex.foreach {
          case (Row(columns), idx) => {
            val row = sheet.createRow(idx)
            columns.zipWithIndex.foreach {
              case (c, colIdx) => {
                val cell = row.createCell(colIdx)
                c match {
                  case Blank             => {}
                  case StringCell(value) => cell.setCellValue(value)
                  case DateTimeCell(Some(date), _) => {
                    cell.setCellStyle(DateTimeCellStyle)
                    cell.setCellValue(date.toDate)
                  }
                  case DateTimeCell(_, _) => {}
                  case bdc @ BigDecimalCell(_, _, _, _) =>
                    bdc.scaled.flatMap(Option(_)).foreach(s => cell.setCellValue(s.doubleValue))
                  case x => throw new IllegalArgumentException("been adding columns, haven't you? %s".format(x.getClass))
                }
              }
            }
          }
        }
        if (autoSize) header.map(_.cells).getOrElse(Nil).zipWithIndex.foreach {
          case (_, i) => sheet.autoSizeColumn(i)
        }
      }
    }
    workbook
  }
  def bytes(ts: AsTableSet): Array[Byte] = {
    val out = new java.io.ByteArrayOutputStream
    apply(ts).write(out)
    out.toByteArray
  }
}
