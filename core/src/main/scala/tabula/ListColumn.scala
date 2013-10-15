package tabula

import Tabula._

abstract class ListColumn[F: Manifest, T, C, Cols](val underlying: List[Column[F, T, C]])(implicit cz: Cellulizer[List[ColumnAndCell[F, T, C]], List[C]]) extends Column[F, List[ColumnAndCell[F, T, C]], List[C]](x => Some(underlying.map(_(x))))
