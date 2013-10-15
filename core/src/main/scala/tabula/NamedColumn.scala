package tabula

import Tabula._
import shapeless._

class NamedColumn[F, T, C, Col](val name: Cell[String], column: Col)(implicit ev: Col <:< Column[F, T, C]) extends Column[F, T, C](column.f)(column.cz, column.mf, column.mc) with ColFun[F, T, C]

object NamedColumn {
  def names[F, T, C, NcT <: HList, Col](cols: Col :: NcT)(implicit ev: Col <:< Column[F, T, C], tl: ToList[Col :: NcT, Column[_, _, _]]): List[Option[String]] =
    cols.toList[Column[_, _, _]].map {
      case named: NamedColumn[_, _, _, _] => named.name.value
      case _                              => None
    }
}
