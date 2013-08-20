package tabula

import com.github.nscala_time.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }
import java.math.{ BigDecimal => JavaBigDecimal }

abstract class Cellulizer[T, C](convert: T => C)(implicit val m: Manifest[C]) {
  cz =>

  private class LazyCell[F](source: F, f: F => Option[T])(implicit val m: Manifest[C]) extends Cell[C] {
    lazy val value = f(source).map(convert)
  }

  def apply[F](source: F, f: F => Option[T]): Cell[C] = new LazyCell(source, f)
  def apply(value: T): Cell[C] = new LazyCell(value, Some(_: T))
  def apply(value: Option[T]): Cell[C] = new LazyCell(value, identity: Option[T] => Option[T])
}

trait Cellulizers {
  implicit object StringStringCellulizer extends Cellulizer[String, String](identity)
  implicit object ScalaBigDecimalBigDecimalCellulizer extends Cellulizer[ScalaBigDecimal, ScalaBigDecimal](identity)
  implicit object JavaBigDecimalBigDecimalCellulizer extends Cellulizer[JavaBigDecimal, ScalaBigDecimal](identity)
  implicit object LongBigDecimalCellulizer extends Cellulizer[Long, ScalaBigDecimal](ScalaBigDecimal(_))
  implicit object IntBigDecimalCellulizer extends Cellulizer[Int, ScalaBigDecimal](ScalaBigDecimal(_))
  implicit object DoubleBigDecimalCellulizer extends Cellulizer[Double, ScalaBigDecimal](ScalaBigDecimal(_))
  implicit object FlaotBigDecimalCellulizer extends Cellulizer[Float, ScalaBigDecimal](ScalaBigDecimal(_))
  implicit object DateTimeDateTimeCellulizer extends Cellulizer[DateTime, DateTime](identity)

  def cellulize[F, T](value: F)(implicit cz: Cellulizer[F, T]): Cell[T] = cz(value)
  def cellulize[F, T](value: Option[F])(implicit cz: Cellulizer[F, T]): Cell[T] = cz(value)
}
