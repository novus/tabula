package tabula.test

import tabula._
import org.specs._

case class A(a: String)

object ColumnOne extends Column[A] {
  val name = "a"
  val label = "A"
  def cell = {
    case Some(A(a)) => Some(StringCell(a))
  }
}

object Up extends Column[StringCell] {
  def cell = {
    case Some(StringCell(s)) => Some(StringCell(s.toUpperCase))
  }
}

case class TimesN(n: Int) extends Column[StringCell] {
  def cell = {
    case Some(StringCell(s)) => Some(StringCell(s * n))
  }
}

class ColumnSpec extends Specification {
  "a column" should {
    "make a cell" in {
      ColumnOne.cell(Some(A("a"))) must beSome[Cell].which {
        case StringCell(a) => a must_== "a"
      }
    }
    "participate in chains" in {
      Columns(ColumnOne, Up :: Nil).cell(Some(A("b"))) must beSome[Cell].which {
        case StringCell(b) => b must_== "B"
      }
    }
    "participate in longer chains" in {
      Columns(Columns(ColumnOne, Up :: Nil), TimesN(2) :: Nil).cell(Some(A("b"))) must beSome[Cell].which {
        case StringCell(b) => b must_== "BB"
      }
      Columns(ColumnOne, Up :: TimesN(2) :: Nil).cell(Some(A("b"))) must beSome[Cell].which {
        case StringCell(b) => b must_== "BB"
      }
    }
  }
  "column DSL" should {
    "optimize longer chains" in {
      val chain = C | A | F | E | B | A | B | E
      chain.first.asInstanceOf[X[_]].x must_== "C"
      chain.columns.size must_== 7
      (chain.first :: chain.columns).map(_.asInstanceOf[X[_]].x).mkString("") must_== "CAFEBABE"
    }
  }
}

abstract class X[F](val x: String) extends Column[F] { def cell = { case _ => Some(Blank) } }
object C extends X[Int]("C")
object A extends X[Cell]("A")
object F extends X[Cell]("F")
object E extends X[Cell]("E")
object B extends X[Cell]("B")
