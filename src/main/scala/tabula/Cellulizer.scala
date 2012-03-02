package tabula

import org.scala_tools.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }
import java.math.{ BigDecimal => JavaBigDecimal }

trait Cellulizer[F, T] {
  def apply(t: F): Cell[T]
}

trait Cellulizers {
  implicit object StringCellulizer extends Cellulizer[String, String] {
    def apply(s: String) = new Cell[String] {
      val value = Option(s)
      val m = implicitly[Manifest[String]]
    }
  }

  implicit object OptStringCellulizer extends Cellulizer[Option[String], String] {
    def apply(s: Option[String]) = new Cell[String] {
      val value = s
      val m = implicitly[Manifest[String]]
    }
  }

  implicit object DateTimeCellulizer extends Cellulizer[DateTime, DateTime] {
    def apply(dt: DateTime) = new Cell[DateTime] {
      val value = Option(dt)
      val m = implicitly[Manifest[DateTime]]
    }
  }

  implicit object OptDateTimeCellulizer extends Cellulizer[Option[DateTime], DateTime] {
    def apply(dt: Option[DateTime]) = new Cell[DateTime] {
      val value = dt
      val m = implicitly[Manifest[DateTime]]
    }
  }

  implicit object ScalaBigDecimalCellulizer extends Cellulizer[ScalaBigDecimal, ScalaBigDecimal] {
    def apply(bd: ScalaBigDecimal) = new Cell[ScalaBigDecimal] {
      val value = Option(bd)
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object OptScalaBigDecimalCellulizer extends Cellulizer[Option[ScalaBigDecimal], ScalaBigDecimal] {
    def apply(bd: Option[ScalaBigDecimal]) = new Cell[ScalaBigDecimal] {
      val value = bd
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object JavaBigDecimalCellulizer extends Cellulizer[JavaBigDecimal, ScalaBigDecimal] {
    def apply(bd: JavaBigDecimal) = new Cell[ScalaBigDecimal] {
      val value = Option(ScalaBigDecimal(bd))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object OptJavaBigDecimalCellulizer extends Cellulizer[Option[JavaBigDecimal], ScalaBigDecimal] {
    def apply(bd: Option[JavaBigDecimal]) = new Cell[ScalaBigDecimal] {
      val value = bd.map(ScalaBigDecimal(_))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object IntCellulizer extends Cellulizer[Int, ScalaBigDecimal] {
    def apply(x: Int) = new Cell[ScalaBigDecimal] {
      val value = Option(ScalaBigDecimal(x))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object OptIntCellulizer extends Cellulizer[Option[Int], ScalaBigDecimal] {
    def apply(x: Option[Int]) = new Cell[ScalaBigDecimal] {
      val value = x.map(ScalaBigDecimal(_))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object LongCellulizer extends Cellulizer[Long, ScalaBigDecimal] {
    def apply(x: Long) = new Cell[ScalaBigDecimal] {
      val value = Option(ScalaBigDecimal(x))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object OptLongCellulizer extends Cellulizer[Option[Long], ScalaBigDecimal] {
    def apply(x: Option[Long]) = new Cell[ScalaBigDecimal] {
      val value = x.map(ScalaBigDecimal(_))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object FloatCellulizer extends Cellulizer[Float, ScalaBigDecimal] {
    def apply(x: Float) = new Cell[ScalaBigDecimal] {
      val value = Option(ScalaBigDecimal(x))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object OptFloatCellulizer extends Cellulizer[Option[Float], ScalaBigDecimal] {
    def apply(x: Option[Float]) = new Cell[ScalaBigDecimal] {
      val value = x.map(ScalaBigDecimal(_))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object DoubleCellulizer extends Cellulizer[Double, ScalaBigDecimal] {
    def apply(x: Double) = new Cell[ScalaBigDecimal] {
      val value = Option(ScalaBigDecimal(x))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  implicit object OptDoubleCellulizer extends Cellulizer[Option[Double], ScalaBigDecimal] {
    def apply(x: Option[Double]) = new Cell[ScalaBigDecimal] {
      val value = x.map(ScalaBigDecimal(_))
      val m = implicitly[Manifest[ScalaBigDecimal]]
    }
  }

  def cellulize[F, T](f: F)(implicit cz: Cellulizer[F, T]): Cell[T] = cz(f)
}