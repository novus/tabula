package tabula

object Tabula extends Cellulizers {
  implicit def optionize[T](t: T): Option[T] = Option(t)
  implicit def nameColumn[F, T, C](args: (String, Column[F, T, C])) =
    NamedColumn(cellulize(args._1), args._2)
}

trait Cell[A] {
  def value: Option[A]
  def m: Manifest[A]
}

abstract class Column[F, T, C](val f: F => Option[T])(implicit val cz: Cellulizer[T, C]) {
  def apply(x: F): Cell[C] = (f andThen cz.apply)(x)

  abstract class Transform[F, T, C1, TT, C2](
    val left: Column[F, T, C1],
    val right: Column[T, TT, C2]) extends Column[F, TT, C2](left.f(_).flatMap(right.f))(right.cz)

  def |[TT, CC](right: Column[T, TT, CC]) = new Transform[F, T, C, TT, CC](this, right) {}
}

case class TableModel[F](header: List[NamedColumn[F, _, _]]) {
  def apply(f: F): Row = Row(header.map(_(f)))
  def apply(fs: List[F]): Table = Table(rows = fs.map(apply))
  def &(next: NamedColumn[F, _, _]) = copy(header :+ next)
  def ++(other: TableModel[F]) = copy(header ::: other.header)
}

case class NamedColumn[F, T, C](name: Cell[String], column: Column[F, T, C]) extends Column[F, T, C](column.f)(column.cz) {
  def &[TT, CC](next: NamedColumn[F, TT, CC]) = TableModel[F](this :: next :: Nil)
}

case class Row(cells: List[Cell[_]])
case class Table(name: Option[String] = None, header: Option[Row] = None, rows: List[Row], footer: Option[Row] = None)
