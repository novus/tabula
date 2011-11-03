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

class ColumnSpec extends Specification {
  "a column" should {
    "make a cell" in {
      ColumnOne(A("a")) must beSome[Cell].which {
        case StringCell(a) => a must_== "a"
      }
    }
  }
}
