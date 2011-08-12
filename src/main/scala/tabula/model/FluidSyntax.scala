// package tabula.model

// import scala.math.{ BigDecimal => ScalaBigDecimal }
// import java.math.{ BigDecimal => JavaBigDecimal }
// import java.text.DecimalFormat
// import com.novus.commons.util._
// import com.novus.commons.util.RichBigDecimal._
// import org.scala_tools.time.Imports._

// case class CanBeColumn(column: Column, next: Option[CanBeColumn] = None) extends Logging {
//   def `!:`(x: Any): CanBeColumn = {
//     any2cbc(x).copy(next = Some(this))
//   }

//   def `#:`(x: Any): CanBeColumn = {
//     x match {
//       case ((a: Any, url: String), text: Option[String]) => linked_any2cbc(a, url, text).copy(next = Some(this))
//       case _ => `!:`(x)
//     }
//   }

//   def asColumns: List[Column] = column :: {
//     next match {
//       case Some(cbc) => cbc.asColumns
//       case _         => Nil
//     }
//   } ::: Nil
// }

// object `package` {
//   val Blank = StringColumn(None)

//   implicit def cols2rbd(cols: List[Column]) = new {
//     private def d0(f: Option[BigDecimal] => Option[BigDecimal]): List[Column] =
//       cols.map {
//         case bdc: BigDecimalColumn => bdc.copy(value = f(bdc.value))
//         case x                     => x
//       }

//     def scaleToPct = d0 {
//       _.scaleToPowerOfTen(2)
//     }

//     def scaleToBps = d0 {
//       _.scaleToPowerOfTen(4)
//     }

//     def scaleToThousands = d0 {
//       _.scaleToPowerOfTen(-3)
//     }

//     def scaleToMillions = d0 {
//       _.scaleToPowerOfTen(-6)
//     }
//   }

//   implicit def bdcpimp(col: Column) = new {
//     def reformat(f: => DecimalFormat): Column = col match {
//       case bdc: BigDecimalColumn => bdc.copy(formatter = f)
//       case _                     => col
//     }

//     def number: Option[BigDecimal] = col match {
//       case bdc: BigDecimalColumn => bdc.scale
//       case _                     => None
//     }

//     def mapNumber(f: Option[BigDecimal] => Option[BigDecimal]): Column = col match {
//       case bdc: BigDecimalColumn => bdc.copy(value = f(bdc.scale))
//       case _                     => col
//     }
//   }

//   implicit def pimpmanybdcs(cols: List[Column]) = new {
//     def reformat(f: => DecimalFormat): List[Column] = cols.map(_.reformat(f))

//     def mapNumber(f: Option[BigDecimal] => Option[BigDecimal]): List[Column] = cols.map(_.mapNumber(f))
//   }

//   def linked_any2cbc(a: Any, url: String, text: Option[String]): CanBeColumn = {
//     any2cbc(a) match {
//       case cbc @ CanBeColumn(lc: LinkableColumn, _) => cbc.copy(column = lc.linkTo(url, text, Nil))
//       case cbc                                      => cbc
//     }
//   }

//   implicit def any2cbc(a: Any): CanBeColumn = {
//     if (a.isInstanceOf[CanBeColumn]) a.asInstanceOf[CanBeColumn]
//     else
//       CanBeColumn(column = {
//         a match {
//           case c: Column                  => c

//           case b: Boolean                 => StringColumn("%s".format(b))

//           case s: String                  => StringColumn(Option(s))
//           case Some(os: String)           => StringColumn(os)

//           case bd: ScalaBigDecimal        => BigDecimalColumn(Option(bd))
//           case Some(obd: ScalaBigDecimal) => BigDecimalColumn(obd)

//           case bd: JavaBigDecimal         => BigDecimalColumn(Option(bd))
//           case Some(obd: JavaBigDecimal)  => BigDecimalColumn(obd)

//           case n: Int                     => BigDecimalColumn(n)
//           case l: Long                    => BigDecimalColumn(l)
//           case f: Float                   => BigDecimalColumn(f)
//           case d: Double                  => BigDecimalColumn(d)

//           case dt: DateTime               => DateTimeColumn(Option(dt))
//           case Some(odt: DateTime)        => DateTimeColumn(Some(odt))

//           case e: Enumeration#Value       => StringColumn(e.toString)

//           case None                       => EmptyColumn()
//           case null                       => EmptyColumn()

//           case x if (x != null)           => StringColumn(x.toString) // fallback: just make it a string column
//         }
//       })
//   }

//   implicit def cbc2row(cbc: CanBeColumn): Row = Row(columns = cbc.asColumns)

//   implicit def cbc2columns(cbc: CanBeColumn): Seq[Column] = cbc.asColumns

//   implicit def things2cbc(as: Seq[_]): Seq[Column] = as.map(any2cbc _).map(_.asColumns.head)

//   implicit def optint2optbd(i: Option[Int]): Option[ScalaBigDecimal] = i.map(ScalaBigDecimal(_))

//   def sortIndexable(a: Indexable, b: Indexable) =
//     a.idx.orElse(b.idx).getOrElse(-1) <= b.idx.orElse(a.idx).getOrElse(-1)
// }

// trait Indexable {
//   val idx: Option[Int]
// }
