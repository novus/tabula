package tabula

import shapeless._
import Tabula._
import com.github.nscala_time.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }

abstract class Output[CellForm] extends Pullback1[CellForm] {
  self =>

  protected def string(cell: Cell[String]): CellForm
  protected def dateTime(cell: Cell[DateTime]): CellForm
  protected def bigDecimal(cell: Cell[BigDecimal]): CellForm

  implicit def forString[F, T, CAC](implicit ev: CAC <:< (Column[F, T, String], Cell[String])) = at[CAC](cac => string(cac._2))
  implicit def forDateTime[F, T, CAC](implicit ev: CAC <:< (Column[F, T, DateTime], Cell[DateTime])) = at[CAC](cac => dateTime(cac._2))
  implicit def forBigDecimal[F, T, CAC](implicit ev: CAC <:< (Column[F, T, BigDecimal], Cell[BigDecimal])) = at[CAC](cac => bigDecimal(cac._2))
}
