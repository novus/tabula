package tabula

import scala.math.{ BigDecimal => ScalaBigDecimal }
import java.math.{ BigDecimal => JavaBigDecimal }
import java.text.DecimalFormat
import org.scala_tools.time.Imports._

case class CanBeColumn(column: Column, next: Option[CanBeColumn] = None) {
  def `!:`(x: Any): CanBeColumn = {
    any2cbc(x).copy(next = Some(this))
  }

  def `#:`(x: Any): CanBeColumn = {
    x match {
      case ((a: Any, url: String), text: Option[String]) => linked_any2cbc(a, url, text).copy(next = Some(this))
      case _ => `!:`(x)
    }
  }

  def asColumns: List[Column] = column :: {
    next match {
      case Some(cbc) => cbc.asColumns
      case _         => Nil
    }
  } ::: Nil
}

object `package` {
  val Blank = StringColumn(None)

  implicit def bdcpimp(col: Column) = new {
    def reformat(f: => DecimalFormat): Column = col match {
      case bdc: BigDecimalColumn => bdc.copy(formatter = f)
      case _                     => col
    }

    def number: Option[BigDecimal] = col match {
      case bdc: BigDecimalColumn => bdc.scaled
      case _                     => None
    }

    def mapNumber(f: Option[BigDecimal] => Option[BigDecimal]): Column = col match {
      case bdc: BigDecimalColumn => bdc.copy(value = f(bdc.scaled))
      case _                     => col
    }
  }

  implicit def pimpmanybdcs(cols: List[Column]) = new {
    def reformat(f: => DecimalFormat): List[Column] = cols.map(_.reformat(f))

    def mapNumber(f: Option[BigDecimal] => Option[BigDecimal]): List[Column] = cols.map(_.mapNumber(f))
  }

  def linked_any2cbc(a: Any, url: String, text: Option[String]): CanBeColumn = {
    any2cbc(a) match {
      case cbc @ CanBeColumn(lc: LinkableColumn, _) => cbc.copy(column = lc.linkTo(url, text, Nil))
      case cbc                                      => cbc
    }
  }

  implicit def any2cbc(a: Any): CanBeColumn = {
    if (a.isInstanceOf[CanBeColumn]) a.asInstanceOf[CanBeColumn]
    else
      CanBeColumn(column = {
        a match {
          case c: Column                  => c

          case b: Boolean                 => StringColumn("%s".format(b))

          case s: String                  => StringColumn(Option(s))
          case Some(os: String)           => StringColumn(os)

          case bd: ScalaBigDecimal        => BigDecimalColumn(Option(bd))
          case Some(obd: ScalaBigDecimal) => BigDecimalColumn(Option(obd))

          case bd: JavaBigDecimal         => BigDecimalColumn(Option(bd).map(ScalaBigDecimal(_)))
          case Some(jbd: JavaBigDecimal)  => BigDecimalColumn(Option(jbd).map(ScalaBigDecimal(_)))

          case n: Int                     => BigDecimalColumn(Some(ScalaBigDecimal(n)))
          case l: Long                    => BigDecimalColumn(Some(ScalaBigDecimal(l)))
          case f: Float                   => BigDecimalColumn(Some(ScalaBigDecimal(f)))
          case d: Double                  => BigDecimalColumn(Some(ScalaBigDecimal(d)))

          case dt: DateTime               => DateTimeColumn(Option(dt))
          case Some(odt: DateTime)        => DateTimeColumn(Some(odt))

          case e: Enumeration#Value       => StringColumn(e.toString)

          case None                       => EmptyColumn()
          case null                       => EmptyColumn()

          case x if (x != null)           => StringColumn(x.toString)
        }
      })
  }

  implicit def cbc2row(cbc: CanBeColumn): Row = Row(columns = cbc.asColumns)

  implicit def cbc2columns(cbc: CanBeColumn): Seq[Column] = cbc.asColumns

  implicit def things2cbc(as: Seq[_]): Seq[Column] = as.map(any2cbc _).map(_.asColumns.head)

  implicit def optint2optbd(i: Option[Int]): Option[ScalaBigDecimal] = i.map(ScalaBigDecimal(_))

  def sortIndexable(a: Indexable, b: Indexable) =
    a.idx.orElse(b.idx).getOrElse(-1) <= b.idx.orElse(a.idx).getOrElse(-1)
}

trait Indexable {
  val idx: Option[Int]
}
