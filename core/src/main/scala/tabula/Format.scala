package tabula

import shapeless._
import Tabula._

/** Facility to format cells, rows, and tables according to an
  * external data interchange format. Examples of such formats include
  * [[CSV]], Excel, JSON, HTML, and so on.
  *
  * [[Format]] is a `shapeless.Poly2` (by way of [[caseRowCell]]),
  * which makes it possible to `foldLeft` over an `HList` of
  * [[Tabula.ColumnAndCell]]s & produce a single [[Row]].
  */
trait Format extends Poly2 with Writers {
  /** Format-specific representation of a cell type. */
  type Base
  type CellT[C] = ColumnAndCell[_, _, C]

  /** Format-specific representation of a row type. */
  type Row

  /** Type class that turns a `C` into a subclass of `[[Base]]`. */
  trait Formatter[C] {
    /** Cell representation that is a subtype of `[[Base]]` but is
      * specific to this particular `C`.
      */
    type Local <: Base

    /** Turn a [C] into [[Local]]. */
    def apply(value: Option[C]): List[Local]

    /** Turn a [[Cell]][C] into [[Local]]. */
    def apply(cell: Cell[C]): List[Local] = apply(cell.value)

    /** Extract [[Cell]][C] from a [[Tabula.ColumnAndCell]] tuple & turn
      * it into a [[Local]].
      */
    def apply(cell: CellT[C]): List[Local] = apply(cell._2)
  }

  /** "Simple" [[Formatter]] subclass which assumes that `[[Local]] =:=
    * [[Base]]`.
    */
  trait SimpleFormatter[C] extends Formatter[C] {
    type Local = Base
  }

  implicit def listFormatter[C](implicit fter: Formatter[C]): Formatter[List[C]]

  /** Convert a single cell directly to a [[Format]]-specific
    * representation, using an implicitly injected [[Format]]-specific
    * [[Formatter]].
    */
  def apply[C](cell: Cell[C])(implicit fter: Formatter[C]) = fter(cell)

  /** Singleton type class-like entity that exposes operations for
    * constructing [[Format]]-specific [[Row]] representations &
    * appending [[Cell]]s to them.
    */
  trait RowProto {
    /** Construct an empty [[Row]]. */
    def emptyRow: Row

    /** */
    def appendCell[C](cell: CellT[C])(row: Row)(implicit fter: Formatter[C]): Row
    object Name extends Column(identity: Option[String] => Option[String])
    def header(names: List[Option[String]])(implicit fter: Formatter[String]) = {
      names
        .iterator
        .map(cellulize[String, String])
        .map(Name -> _)
        .foldLeft(RowProto.emptyRow)(
          (r, c) => RowProto.appendCell[String](c)(r))
    }
  }

  val RowProto: RowProto

  implicit def caseRowCell[F, T, C](implicit fter: Formatter[C]) =
    at[Row, ColumnAndCell[F, T, C]]((r, c) => RowProto.appendCell(c)(r))
}
