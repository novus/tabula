package tabula

import Tabula._
import shapeless._
import shapeless.ops.hlist._

/** A column that has a name, most commonly produced by calling the
  * `[[Column.@@]]` method on a column.
  */
class NamedColumn[F, T, C, Col](val name: Cell[String], val underlying: Col)(implicit ev: Col <:< Column[F, T, C]) extends Column[F, T, C](underlying.f)(underlying.cz, underlying.mf) with ColFun[F, T, C]

object NamedColumn {
  def names(cols: List[Column[_, _, _]]): List[Option[String]] =
    cols.flatMap {
      case list: ListColumn[_, _, _, _] => names(list.underlying)
      case named: NamedColumn[_, _, _, _] =>
        named.underlying match {
          case list: ListColumn[_, _, _, _] => names(list.underlying)
          case _                            => named.name.value :: Nil
        }
      case _ => None :: Nil
    }
  def names[F, T, C, NcT <: HList, Col](cols: Col :: NcT)(implicit ev: Col <:< Column[F, T, C], tl: ToList[Col :: NcT, Column[_, _, _]]): List[Option[String]] = names(cols.toList[Column[_, _, _]])
}
