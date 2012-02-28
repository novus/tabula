package tabula

import scala.xml._

trait Cell

case class NodeSeqCell(value: NodeSeq) extends Cell
