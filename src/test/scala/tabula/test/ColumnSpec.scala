package tabula.test

import tabula._
import org.specs._

case class A(a: String)

object ColumnOne extends Column[A] {
  val name = "a"
  val label = "A"
  def apply(x: Option[A]) = x.map { case A(a) => StringCell(Some(a)) }
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
