package tabula

object Tabula extends Cellulizers {
  implicit def nameColumn[F, T](args: (String, Column[F, T])) =
    NamedColumn(cellulize(args._1), args._2)
}

trait Cell[A] {
  self =>
  def value: Option[A]
  def map[B](f: A => B)(implicit cz: Cellulizer[Option[B], B]): Cell[B] = cz(value.map(f))
  def flatMap[B](f: Option[A] => Option[B])(implicit cz: Cellulizer[Option[B], B]): Cell[B] = (f andThen cz.apply)(value)
}

abstract class Column[F, T](val f: F => T)(implicit val cz: Cellulizer[T, T]) {
  def apply(x: F): Cell[T] = (f andThen cz.apply)(x)

  abstract class ValueTransform[F, T, TT](
    val left: Column[F, T],
    val right: Column[T, TT]) extends Column[F, TT](
    left.f andThen right.f)(right.cz)
  def |[TT](right: Column[T, TT]) = new ValueTransform[F, T, TT](this, right) {}
}

case class TableModel[F](header: List[NamedColumn[F, _]]) {
  def apply(f: F): Row = Row(header.map(_(f)))
  def apply(fs: List[F]): List[Row] = fs.map(apply)
  def ||(next: NamedColumn[F, _]) = copy(header = header :+ next)
}

case class NamedColumn[F, T](name: Cell[String], column: Column[F, T]) extends Column[F, T](column.f)(column.cz) {
  def ||[TT](next: NamedColumn[F, TT]) = TableModel[F](this :: next :: Nil)
}

case class Row(cells: List[Cell[_]])
case class Table(name: String, header: Option[Row] = None, rows: List[Row], footer: Option[Row] = None)
