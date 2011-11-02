package tabula.test

import tabula._
import org.specs._

case class A(a: String)
object ColumnA extends Column[A] {
  val name = "a"
  val label = "A"
  def apply(a: A) = Some(StringCell(Some(a.a)))
}

class ColumnSpec extends Specification {
  "a column" should {
    "make a cell" in {
      ColumnA(A("a")) must beSome[StringCell].which {
        case StringCell(a) => a must_== "a"
      }
    }
  }
}
