package tabula.model

import scala.xml._

import org.scala_tools.time.Imports._
import com.mongodb.casbah.commons.Imports._

object Row {
  def apply(columns: Seq[Column]): Row = new Row(columns = columns)
}

case class Row(_id: ObjectId = new ObjectId,
               tableId: Option[ObjectId] = None,
               columns: Seq[Column],
               idx: Option[Int] = None) {

  lazy val asCSV = columns.map(_.format).mkString(",")
  lazy val values = columns.map { case StringColumn(value) => value }.toList
}

case class EmptyColumn(value: String = "") extends Column with HasValue with DumbValueFormatter

case class NodeSeqColumn(value: NodeSeq) extends Column with HasValue {
  val format = ""
}
