package tabula

import org.scala_tools.time.Imports._
import scala.math.{ BigDecimal => ScalaBigDecimal }

abstract class Output {
  type CellForm
  type RowForm
  type TableForm

  abstract class Format[T] {
    type Form <: CellForm
    def apply(x: Option[T]): Form
    def apply(c: Cell[T]): Form = apply(c.value)
  }

  implicit val StringFormat: Format[String]
  implicit val DateTimeFormat: Format[DateTime]
  implicit val BigDecimalFormat: Format[ScalaBigDecimal]

  implicit def manifest2format[T](m: Manifest[_]): Format[T] = {
    m.erasure match {
      case c if c == classOf[String]          => implicitly[Format[String]]
      case c if c == classOf[DateTime]        => implicitly[Format[DateTime]]
      case c if c == classOf[ScalaBigDecimal] => implicitly[Format[ScalaBigDecimal]]
      case x                                  => sys.error("can't format Cell[%s]".format(x.getName))
    }
  }.asInstanceOf[Format[T]]

  def apply[T](c: Cell[T])(implicit fmt: Format[T]): CellForm = fmt(c)
  def apply(cs: List[Cell[_]]): List[CellForm] = cs.map(c => apply(c)(c.m))
  def apply(row: Row): RowForm
  def apply(table: Table): TableForm
}
