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

  /** Format-specific representation of a row type. */
  type Row

  /** Type class that turns a `C` into a subclass of `[[Base]]`. */
  abstract class Formatter[C](implicit val manifest: Manifest[C]) {
    /** Cell representation that is a subtype of `[[Base]]` but is
      * specific to this particular `C`.
      */
    type Local <: Base

    /** Turn a [C] into [[Local]]. */
    def apply(value: Option[C]): List[Local]

    /** Turn a [[Cell]][C] into [[Local]]. */
    def apply(cell: Cell[C]): List[Local] = apply(cell.value)

    private[tabula] def format(cell: Cell[C]): List[Local] = apply(cell)
  }

  /** "Simple" [[Formatter]] subclass which assumes that `[[Local]] =:=
    * [[Base]]`.
    */
  trait SimpleFormatter[C] extends Formatter[C] {
    type Local = Base
  }

  implicit def listFormatter[C: Manifest](implicit fter: Formatter[C]): Formatter[List[C]] =
    new Formatter[List[C]] {
      type Local = fter.Local
      def apply(value: Option[List[C]]): List[Local] =
        value match {
          case None         => Nil
          case Some(values) => values.map(Some(_)).map(fter(_)).flatten
        }
    }

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

    def appendCell[C](cell: Cell[C])(row: Row)(implicit fter: Formatter[C]): Row

    def appendBase[T <: Base](value: T)(row: Row): Row

    def header(names: List[Option[String]])(implicit fter: Formatter[String]) = {
      names
        .iterator
        .map(cellulize[String, String])
        .foldLeft(RowProto.emptyRow)(
          (r, c) => RowProto.appendCell[String](c)(r))
    }
  }

  val RowProto: RowProto

  implicit def caseRowCell[F, T, C](implicit fter: Formatter[C]) =
    at[Row, ColumnAndCell[F, T, C]] {
      case (row, (_, cell)) => RowProto.appendCell(cell)(row)
    }
}
