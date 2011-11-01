package tabula

import scala.xml._

import org.scala_tools.time.Imports._
import com.mongodb.casbah.commons.Imports._

case class Row(columns: List[Column]) {
  lazy val asCSV = columns.map(_.format).mkString(",")
  lazy val values = columns.map { case StringColumn(value) => value }.toList
}

case class EmptyColumn(value: String = "") extends Column with HasValue with DumbValueFormatter

case class NodeSeqColumn(value: NodeSeq) extends Column with HasValue {
  val format = ""
}
