package tabula

import scala.xml._

import org.scala_tools.time.Imports._
import com.mongodb.casbah.commons.Imports._

case class Row(columns: List[Cell]) {
  lazy val asCSV = columns.map(_.format).mkString(",")
  lazy val values = columns.map { case StringCell(value) => value }.toList
}

case class EmptyCell(value: String = "") extends Cell with HasValue with DumbValueFormatter

case class NodeSeqCell(value: NodeSeq) extends Cell with HasValue {
  val format = ""
}
