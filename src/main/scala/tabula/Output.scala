package tabula

import org.scala_tools.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }

abstract class Output {
  abstract class Format[T] {
    type Form
    def apply(x: Option[T]): Form
    def apply(c: Cell[T]): Form = apply(c.value)
  }

  implicit val StringFormat: Format[String]
  implicit val DateTimeFormat: Format[DateTime]
  implicit val BigDecimalFormat: Format[ScalaBigDecimal]

  implicit def manifest2format[T](m: Manifest[_]): Format[T] = {
    m.erasure match {
      case c if c == classOf[String] => implicitly[Format[String]]
      case x                         => sys.error("can't format Cell[%s]".format(x.getName))
    }
  }.asInstanceOf[Format[T]]

  def apply[T](c: Cell[T])(implicit fmt: Format[T]) = fmt(c)
}
