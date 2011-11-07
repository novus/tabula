package tabula

import scala.xml._
import org.scala_tools.time.Imports._

case class Row(cells: List[Cell]) {
  lazy val asCSV = cells.map(_.format).mkString(",")
  lazy val values = cells.map { case StringCell(value) => value }.toList
}

case class NodeSeqCell(value: NodeSeq) extends Cell with HasValue {
  val format = ""
}
