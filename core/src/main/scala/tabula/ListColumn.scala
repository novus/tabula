package tabula

import Tabula._

/** Wrapper around a List[Column] that allows a single column to
  * produce a list of [[Cell]]s the length of which will only be known
  * at runtime. Requires appropriate support within [[Format]] and
  * [[Cellulizer]] in order to enable other Tabula components to work
  * with cells of type [[Cell]][List[_]].
  */
abstract class ListColumn[F, T, C, Cols](val underlying: List[Column[F, T, C]])(implicit mf: Manifest[F], cz: Cellulizer[List[ColumnAndCell[F, T, C]], List[C]]) extends Column[F, List[ColumnAndCell[F, T, C]], List[C]](x => Some(underlying.map(_(x))))
