package tabula

case class Row(cells: List[Cell]) {
  lazy val values = cells.map { case StringCell(value) => value }.toList
}

