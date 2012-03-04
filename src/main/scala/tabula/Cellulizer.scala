package tabula

import org.scala_tools.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }
import java.math.{ BigDecimal => JavaBigDecimal }

trait Cellulizer[F, T] {
  def apply(t: Option[F]): Cell[T]
  implicit def convert(f: Option[F]): Cell[T] = apply(f)
  implicit def convert(f: F): Cell[T] = apply(Option(f))
}

trait Cellulizers {
  implicit object OptStringCellulizer extends Cellulizer[String, String] {
    def apply(s: Option[String]) = new Cell[String] {
      val value = s
      val m = implicitly[Manifest[String]]
    }
  }

  implicit object OptDateTimeCellulizer extends Cellulizer[DateTime, DateTime] {
    def apply(dt: Option[DateTime]) = new Cell[DateTime] {
      val value = dt
      val m = implicitly[Manifest[DateTime]]
    }
  }

  implicit object OptScalaBigDecimalCellulizer extends Cellulizer[ScalaBigDecimal, ScalaBigDecimal] {
    def apply(bd: Option[ScalaBigDecimal]) = new Cell[ScalaBigDecimal] {
      val value = bd
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object OptJavaBigDecimalCellulizer extends Cellulizer[JavaBigDecimal, ScalaBigDecimal] {
    def apply(bd: Option[JavaBigDecimal]) = new Cell[ScalaBigDecimal] {
      val value = bd.map(ScalaBigDecimal(_))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object OptIntCellulizer extends Cellulizer[Int, ScalaBigDecimal] {
    def apply(x: Option[Int]) = new Cell[ScalaBigDecimal] {
      val value = x.map(ScalaBigDecimal(_))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object OptLongCellulizer extends Cellulizer[Long, ScalaBigDecimal] {
    def apply(x: Option[Long]) = new Cell[ScalaBigDecimal] {
      val value = x.map(ScalaBigDecimal(_))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object OptFloatCellulizer extends Cellulizer[Float, ScalaBigDecimal] {
    def apply(x: Option[Float]) = new Cell[ScalaBigDecimal] {
      val value = x.map(ScalaBigDecimal(_))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object OptDoubleCellulizer extends Cellulizer[Double, ScalaBigDecimal] {
    def apply(x: Option[Double]) = new Cell[ScalaBigDecimal] {
      val value = x.map(ScalaBigDecimal(_))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  def cellulize[F, T](f: Option[F])(implicit cz: Cellulizer[F, T]): Cell[T] = cz(f)
}
