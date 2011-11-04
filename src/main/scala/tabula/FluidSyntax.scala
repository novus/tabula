package tabula

import scala.math.{ BigDecimal => ScalaBigDecimal }
import java.math.{ BigDecimal => JavaBigDecimal }
import java.text.DecimalFormat
import org.scala_tools.time.Imports._

case class CanBeCell(column: Cell, next: Option[CanBeCell] = None) {
  def `!:`(x: Any): CanBeCell = {
    any2cbc(x).copy(next = Some(this))
  }

  def `#:`(x: Any): CanBeCell = {
    x match {
      case ((a: Any, url: String), text: Option[String]) => linked_any2cbc(a, url, text).copy(next = Some(this))
      case _ => `!:`(x)
    }
  }

  def asCells: List[Cell] = column :: {
    next match {
      case Some(cbc) => cbc.asCells
      case _         => Nil
    }
  } ::: Nil
}

object `package` {
  val Blank = StringCell(None)
  type CellFun[F] = Option[F] => Option[Cell]
  type ColumnsChain[C] = List[Column[C]]

  implicit def wtf(c: Column[_]): Column[Cell] = c.asInstanceOf[Column[Cell]]

  implicit def colpimp[F](col: Column[F]) = new {
    def |>[N <: Cell](next: Column[N]) = Columns(col, next :: Nil)
  }

  implicit def bdcpimp(col: Cell) = new {
    def reformat(f: => DecimalFormat): Cell = col match {
      case bdc: BigDecimalCell => bdc.copy(formatter = f)
      case _                   => col
    }

    def number: Option[BigDecimal] = col match {
      case bdc: BigDecimalCell => bdc.scaled
      case _                   => None
    }

    def mapNumber(f: Option[BigDecimal] => Option[BigDecimal]): Cell = col match {
      case bdc: BigDecimalCell => bdc.copy(value = f(bdc.scaled))
      case _                   => col
    }
  }

  implicit def pimpmanybdcs(cols: List[Cell]) = new {
    def reformat(f: => DecimalFormat): List[Cell] = cols.map(_.reformat(f))

    def mapNumber(f: Option[BigDecimal] => Option[BigDecimal]): List[Cell] = cols.map(_.mapNumber(f))
  }

  def linked_any2cbc(a: Any, url: String, text: Option[String]): CanBeCell = {
    any2cbc(a) match {
      case cbc @ CanBeCell(lc: LinkableCell, _) => cbc.copy(column = lc.linkTo(url, text, Nil))
      case x                                    => x
    }
  }

  implicit def any2cbc(a: Any): CanBeCell = {
    if (a.isInstanceOf[CanBeCell]) a.asInstanceOf[CanBeCell]
    else
      CanBeCell(column = {
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

  implicit def cbc2row(cbc: CanBeCell): Row = Row(columns = cbc.asCells)

  implicit def cbc2columns(cbc: CanBeCell): Seq[Cell] = cbc.asCells

  implicit def things2cbc(as: Seq[_]): Seq[Cell] = as.map(any2cbc _).map(_.asCells.head)

  implicit def optint2optbd(i: Option[Int]): Option[ScalaBigDecimal] = i.map(ScalaBigDecimal(_))

  def sortIndexable(a: Indexable, b: Indexable) =
    a.idx.orElse(b.idx).getOrElse(-1) <= b.idx.orElse(a.idx).getOrElse(-1)
}

trait Indexable {
  val idx: Option[Int]
}
