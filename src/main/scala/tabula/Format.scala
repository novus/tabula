package tabula

import shapeless._
import Tabula._

trait Format[B] extends Pullback1[B] {
  type Base = B

  trait Formatter[C] {
    def apply(cell: Cell[C]): Base
  }

  def apply[C](cell: Cell[C])(implicit fter: Formatter[C]) = fter(cell)

  implicit def default[F, T, CAC, C](implicit ev: CAC <:< (Column[F, T, C], Cell[C]), fter: Formatter[C]) =
    at[CAC](cac => fter(cac._2))
}
