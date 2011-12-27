package tabula

import scala.xml._

trait HTML extends Output[NodeSeq] {
  type Input = Table

  def title: String
  def classes: List[String]
  def id: Option[String]
  def placeholder: Option[String]

  def thead(table: Table) = {
    if (table.rows.size > 0)
      <thead>{ table.header.map(row(table, _)(xml => <th> { xml }</th>)).getOrElse(NodeSeq.Empty) }</thead>
    else
      NodeSeq.Empty
  }

  def tbody(table: Table) = {
    if (table.rows.size > 0)
      <tbody>{ table.rows.map(row(table, _)(xml => <tr>{ xml }</tr>)) }</tbody>
    else
      <tbody>{ placeholder orNull }</tbody>
  }

  def row(table: Table, r: Row)(wrapper: NodeSeq => NodeSeq) = wrapper(r.cells.flatMap(cell))

  def cellulize(classes: List[String])(f: => NodeSeq): NodeSeq =
    <td class={ if (classes.isEmpty) null else classes.mkString(" ") }>{ f }</td>

  implicit def string2text(s: String) = Text(s)

  def cell(c: Cell): NodeSeq = {
    c match {
      case StringCell(value)              => cellulize(Nil)(value)
      case dtc @ DateTimeCell(Some(_), _) => cellulize(Nil)(dtc.format)
      case NodeSeqCell(xml)               => cellulize(Nil)(xml)
      case bdc @ BigDecimalCell(Some(_), _, _, _) => {
        val classes = "numeric" :: (
          if (bdc.positive_?) "positive" :: Nil
          else if (bdc.negative_?) "negative" :: Nil
          else Nil)

        cellulize(classes) {
          if (bdc.zero_? || !bdc.scaled.isDefined || bdc.displayZero_?)
            <span>&nbsp;&nbsp;-&nbsp;&nbsp;</span>
          else {
            <span>{ bdc.human }</span>
          }
        }
      }
      case _ => cellulize(Nil)(NodeSeq.Empty)
    }
  }

  def tfoot(table: Table): NodeSeq = <tfoot/>

  def apply(table: Table) =
    table match {
      case Table(name, header, rows, footer) =>
        <div id={ id orNull }>
          <h2>{ title }</h2>
          <table>
            { thead(table) }
            { tbody(table) }
            { tfoot(table) }
          </table>
        </div>
    }
}
