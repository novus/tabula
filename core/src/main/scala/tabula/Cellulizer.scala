package tabula

import org.joda.time._
import scala.math.{ BigDecimal => ScalaBigDecimal }
import java.math.{ BigDecimal => JavaBigDecimal }

abstract class Cellulizer[T, C](convert: T => C)(implicit mc: Manifest[C]) {
  cz =>

  private class LazyCell[F](source: F, f: F => Option[T]) extends Cell[C] {
    def m = mc
    lazy val value = f(source).map(convert)
  }

  def apply[F](source: F, f: F => Option[T]): Cell[C] = new LazyCell(source, f)
  def apply(value: T): Cell[C] = new LazyCell(value, Some(_: T))
  def apply(value: Option[T]): Cell[C] = new LazyCell(value, identity: Option[T] => Option[T])
}

trait Cellulizers {
  implicit object StringStringCellulizer extends Cellulizer[String, String](identity)
  implicit object ScalaBigDecimalDoubleCellulizer extends Cellulizer[ScalaBigDecimal, Double](_.doubleValue)
  implicit object JavaBigDecimalDoubleCellulizer extends Cellulizer[JavaBigDecimal, Double](_.doubleValue)
  implicit object LongDoubleCellulizer extends Cellulizer[Long, Double](_.doubleValue)
  implicit object IntDoubleCellulizer extends Cellulizer[Int, Double](_.doubleValue)
  implicit object DoubleDoubleCellulizer extends Cellulizer[Double, Double](identity)
  implicit object FlaotDoubleCellulizer extends Cellulizer[Float, Double](_.doubleValue)
  implicit object DateTimeDateTimeCellulizer extends Cellulizer[DateTime, DateTime](identity)

  def cellulize[F, T](value: F)(implicit cz: Cellulizer[F, T]): Cell[T] = cz(value)
  def cellulize[F, T](value: Option[F])(implicit cz: Cellulizer[F, T]): Cell[T] = cz(value)
}
