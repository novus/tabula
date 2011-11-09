package tabula.dsl

import tabula._
import scala.math.{ BigDecimal => ScalaBigDecimal }
import java.math.{ BigDecimal => JavaBigDecimal }
import org.scala_tools.time.Imports._

case class CanBeCell(cell: Cell, next: Option[CanBeCell] = None) {
  def `!:`(x: Any): CanBeCell = {
    any2cbc(x).copy(next = Some(this))
  }

  def `#:`(x: Any): CanBeCell = {
    x match {
      case ((a: Any, url: String), text: Option[String]) => linked_any2cbc(a, url, text).copy(next = Some(this))
      case _ => `!:`(x)
    }
  }

  def asCells: List[Cell] = cell :: {
    next match {
      case Some(cbc) => cbc.asCells
      case _         => Nil
    }
  } ::: Nil
}

object `package` {
  def linked_any2cbc(a: Any, url: String, text: Option[String]): CanBeCell = {
    any2cbc(a) match {
      case cbc @ CanBeCell(lc: LinkableCell, _) => cbc.copy(cell = lc.linkTo(url, text, Nil))
      case x                                    => x
    }
  }

  implicit def any2cbc(a: Any): CanBeCell = {
    if (a.isInstanceOf[CanBeCell]) a.asInstanceOf[CanBeCell]
    else
      CanBeCell(cell = {
        a match {
          case c: Cell                    => c

          case b: Boolean                 => StringCell("%s".format(b))

          case s: String                  => StringCell(Option(s))
          case Some(os: String)           => StringCell(os)

          case bd: ScalaBigDecimal        => BigDecimalCell(Option(bd))
          case Some(obd: ScalaBigDecimal) => BigDecimalCell(Option(obd))

          case bd: JavaBigDecimal         => BigDecimalCell(Option(bd).map(ScalaBigDecimal(_)))
          case Some(jbd: JavaBigDecimal)  => BigDecimalCell(Option(jbd).map(ScalaBigDecimal(_)))

          case n: Int                     => BigDecimalCell(Some(ScalaBigDecimal(n)))
          case l: Long                    => BigDecimalCell(Some(ScalaBigDecimal(l)))
          case f: Float                   => BigDecimalCell(Some(ScalaBigDecimal(f)))
          case d: Double                  => BigDecimalCell(Some(ScalaBigDecimal(d)))

          case dt: DateTime               => DateTimeCell(Option(dt))
          case Some(odt: DateTime)        => DateTimeCell(Some(odt))

          case e: Enumeration#Value       => StringCell(e.toString)

          case None | null                => Blank

          case x if (x != null)           => StringCell(x.toString)
        }
      })
  }

  implicit def cbc2row(cbc: CanBeCell): Row = Row(cbc.asCells)
  implicit def cbc2cells(cbc: CanBeCell): Seq[Cell] = cbc.asCells
  implicit def things2cbc(as: Seq[_]): Seq[Cell] = as.map(any2cbc _).map(_.asCells.head)
  implicit def optint2optbd(i: Option[Int]): Option[ScalaBigDecimal] = i.map(ScalaBigDecimal(_))
}
