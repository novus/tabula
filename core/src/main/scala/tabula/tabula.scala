package tabula

import Tabula._
import shapeless._
import shapeless.HList._
import shapeless.Poly._

object Tabula extends Cellulizers with Aggregators {
  type ColumnAndCell[F, T, C] = (Column[F, T, C], Cell[C])
  type ColFun[F, T, C] = F => ColumnAndCell[F, T, C]

  def cells[F, T, C, NcT <: HList, O <: HList, Col](cols: Col :: NcT)(implicit ev: Col <:< Column[F, T, C], aa: ApplyAll[F, Col :: NcT, ColumnAndCell[F, T, C] :: O]): F => ColumnAndCell[F, T, C] :: O =
    (x: F) => ApplyAll(x)(cols)

  implicit def pimpCells[F, T, C, O <: HList](cells: ColumnAndCell[F, T, C] :: O) = new {
    type Cells = ColumnAndCell[F, T, C] :: O
    def row[Fmt <: Format](format: Fmt)(implicit ev: Fmt <:< Format, lf: LeftFolder[Cells, format.Row, Fmt]) =
      cells.foldLeft(format.RowProto.emptyRow)(format)
  }
}

/** General-purpose cell abstraction that ships a potentially missing
  * value of a table cell. Cell[A] serves as a lingua franca for all
  * Tabula components downstream of [[tabula.Column]].
  */
trait Cell[A] {
  cell =>

  /** The table cell's value. Since all cell values are nullable, all
    * consumers must be able to handle missing values.
    */
  def value: Option[A]

  /** Witness of original compile-time type of A. */
  def m: Manifest[A]

  /** Transforms Cell[A] to Cell[B].  Missing A will result in missing
    * B. Use [[flatMap]] to coerce a present A into a (potentially)
    * missing B.
    */
  def map[B: Manifest](f: A => B): Cell[B] = new Cell[B] {
    lazy val value = cell.value.map(f)
    lazy val m = manifest[B]
  }

  /** Transforms Cell[A] to Cell[B]. Missing A will result in missing
    * B. At its discretion, `f` may decide to drop the B by returning
    * None.
    */
  def flatMap[B: Manifest](f: A => Option[B]): Cell[B] = new Cell[B] {
    lazy val value = cell.value.flatMap(f)
    lazy val m = manifest[B]
  }
}

object Column {
  implicit def optionize[T](t: T): Option[T] = Option(t)
}

/** Column abstraction. Brings together unidirectional type-safe
  * conversions between the following three types:
  *
  * - `F` (mnemonic From): This is the application's domain type from
  * which cell data is extracted.
  *
  * - `T` (mnemonic Transfer): Intermediate type for which a
  * `[[Cellulizer]][T, C]` typeclass instance exists.
  *
  * - `C` (mnemonic Cell): The final Cell type that will be used by
  * various [[tabula.Format]] implementations in the course of
  * formatting table & cell data to pluggable destination formats.
  *
  * In general, the Column class encapsulates the `F->T` conversion,
  * as shown in the below schematic:
  *
  * (From type) `--Column-->` (Transfer type) `--Cellulizer-->` (Cell type)
  *
  * The last leg (`T->C`) is provided by an implicitly injected
  * `[[Cellulizer]][T, C]`.
  *
  * It's important to understand the rationale behind using the `F`,
  * `T` and `C` types to organize transitions that bring a domain
  * model into the tabular world of Tabula. Let's start with `C`:
  * there are only a few types of cells in any spreadsheet- or
  * table-like application: numbers, dates/times, and
  * strings. Straight out of the box, Tabula provides support for a
  * few of these in the form of `Double`, Joda `DateTime`, and
  * `String` respectively.
  *
  * Building on the idea that [[Cell]][C] is the lingua franca of
  * components that work with cells, we can treat the `C` type param
  * as the lingua franca of cells: each Tabula component or extension
  * needs only concern itself with a limited number of cell types,
  * which might come from an almost unlimited number of source data
  * types.
  *
  * Similarly, arriving at the `C` type might not be the most trivial
  * of tasks in some applications. In particular, there is no way to
  * predict the total set of paths a value might take between `F` and
  * `C`. Thus the `T` intermediate representation type serves to make
  * it easier to bridge the gap between `F` and `C`.
  *
  * To illustrate, consider an arbitrary `F` that somehow produces
  * values of type `String`. Since tabula already supports strings out
  * of the box to enable `Cell[String]`, the `T->C` transformation is
  * actually `identity`.
  *
  * A more complex case is any kind of numeric value: Let's say that
  * there's a `F` that contains values of type `Int`, but Tabula only
  * supports `Cell[Double]` as its sole numeric type. In this case,
  * it's obvious that `T =:= Int`, so an implicit `[[Cellulizer]][Int,
  * Double]` needs to be in scope to enable the use of a `Column[F,
  * Int, Double]`.
  *
  * The upside of this design is that any particular `F` and its
  * transformation from domain type to a cell doesn't need to be
  * explicitly and inextricably dependent on the fact that only a
  * limited set of `[[Cell]][C]` types exist within the Tabula
  * domain. Such transformation can be extracted out & provided
  * separately (and implicitly) in order to simply the job of defining
  * columns.
  *
  * In an application that warrants a more complex transition between
  * `T` and `C`, the integrators of Tabula will bear the burden of
  * implementing the various `[[Cellulizer]][T, C]` type class
  * instances. This arrangement makes the cost of such integration
  * finite and easily quantifiable, while keeping the integration from
  * becoming a behavior intrinsic to the application's own domain
  * types.
  */
abstract class Column[F, T, C](val f: F => Option[T])(implicit val cz: Cellulizer[T, C], val mf: Manifest[F], val mc: Manifest[C]) extends ColFun[F, T, C] {
  def apply(source: F): ColumnAndCell[F, T, C] = this -> cz(source, f)

  class Transform[TT, CC](next: Column[T, TT, CC])(implicit mcc: Manifest[CC]) extends Column[F, TT, CC](f(_).flatMap(next.f))(next.cz, mf, mcc)

  def |[TT, CC](next: Column[T, TT, CC])(implicit mcc: Manifest[CC]) = new Transform[TT, CC](next)

  def `@@`(name: String) = new NamedColumn(cellulize(name), this)
}

class NamedColumn[F, T, C, Col](val name: Cell[String], column: Col)(implicit ev: Col <:< Column[F, T, C]) extends Column[F, T, C](column.f)(column.cz, column.mf, column.mc) with ColFun[F, T, C]

object NamedColumn {
  def names[F, T, C, NcT <: HList, Col](cols: Col :: NcT)(implicit ev: Col <:< Column[F, T, C], tl: ToList[Col :: NcT, Column[_, _, _]]): List[Option[String]] =
    cols.toList[Column[_, _, _]].map {
      case named: NamedColumn[_, _, _, _] => named.name.value
      case _                              => None
    }
}
