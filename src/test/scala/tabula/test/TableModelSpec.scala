package tabula.test

import tabula._
import org.specs._

case class Person(first: String, last: String, age: Int)

class NameColumn(f: Person => String) extends Column[Person] {
  def cell = { case Some(p) => Some(StringCell(f(p))) }
}

object FirstName extends NameColumn(_.first)
object LastName extends NameColumn(_.last)

object Capitalize extends Column[StringCell] {
  def cell = {
    case Some(StringCell(name)) => Some(StringCell({
      name.split("").drop(1).toList match {
        case head :: tail => head.toUpperCase + tail.mkString("").toLowerCase
        case _            => name
      }
    }))
  }
}

object Age extends Column[Person] {
  def cell = { case Some(Person(_, _, age)) => Some(BigDecimalCell(age)) }
}

case class Averaged[F](column: Column[F]) extends Aggregated[F, BigDecimalCell] {
  def fun = {
    case cells => {
      Some(BigDecimalCell(Some(
        BigDecimal(cells.map { case (Person(_, _, age), _) => age }.sum / cells.size))))
    }
  }
}

case class TableModelSpecData(howMany: Int) {
  def mkage(n: Int) = (n + 7) * 30 / 4
  lazy val people = (0 until howMany).toList.map {
    n => Person("mR. %d".format(n), "bOvEy %d".format(n), age = mkage(n))
  }
  lazy val model = TableModel(List(FirstName | Capitalize, LastName | Capitalize, Averaged(Age)))
  lazy val table = model.table(people)
}

class TableModelSpec extends Specification {
  "a table model" should {
    "create tables" in {
      val data = TableModelSpecData(51)
      import data._
      println(table.asCSV)
      table.rows mustNot beEmpty
      for ((row, idx) <- table.rows.zipWithIndex) {
        row.cells.size must_== 3
        row.cells match {
          case StringCell(first) :: StringCell(last) :: BigDecimalCell(Some(age), _, _, _) :: Nil => {
            first must_== "Mr. %d".format(idx)
            last must_== "Bovey %d".format(idx)
            age must_== mkage(idx)
          }
          case _ => fail
        }
      }
      table.footer.flatMap(_.cells.lastOption) must beSome[Cell].which {
        case BigDecimalCell(Some(avg), _, _, _) => avg must_== people.map(_.age).sum / people.size
      }
    }
  }
}
