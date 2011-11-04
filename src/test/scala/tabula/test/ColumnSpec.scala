package tabula.test

import tabula._
import org.specs._

case class A(a: String)

object ColumnOne extends Column[A] {
  val name = "a"
  val label = "A"
  def apply: CellFun[A] = {
    case Some(A(a)) => Some(StringCell(a))
  }
}

object Up extends Column[StringCell] {
  def apply = {
    case Some(StringCell(s)) => Some(StringCell(s.toUpperCase))
  }
}

case class TimesN(n: Int) extends Column[StringCell] {
  def apply = {
    case Some(StringCell(s)) => Some(StringCell(s * n))
  }
}

class ColumnSpec extends Specification {
  "a column" should {
    "make a cell" in {
      ColumnOne.apply(Some(A("a"))) must beSome[Cell].which {
        case StringCell(a) => a must_== "a"
      }
    }
    "participate in chains" in {
      Columns(ColumnOne, Up :: Nil).apply(Some(A("b"))) must beSome[Cell].which {
        case StringCell(b) => b must_== "B"
      }
    }
    "participate in longer chains" in {
      Columns(Columns(ColumnOne, Up :: Nil), TimesN(2) :: Nil).apply(Some(A("b"))) must beSome[Cell].which {
        case StringCell(b) => b must_== "BB"
      }
      Columns(ColumnOne, Up :: TimesN(2) :: Nil).apply(Some(A("b"))) must beSome[Cell].which {
        case StringCell(b) => b must_== "BB"
      }
    }
  }
}
