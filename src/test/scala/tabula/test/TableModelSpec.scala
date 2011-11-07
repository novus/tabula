package tabula.test

import tabula._
import org.specs._

case class Person(first: String, last: String, age: Int)

class NameColumn(f: Person => String) extends Column[Person] {
  def apply = { case Some(p) => Some(StringCell(f(p))) }
}

object FirstName extends NameColumn(_.first)
object LastName extends NameColumn(_.last)

object Capitalize extends Column[StringCell] {
  def apply = {
    case Some(StringCell(name)) => Some(StringCell({
      name.split("").drop(1).toList match {
        case head :: tail => head.toUpperCase + tail.mkString("").toLowerCase
        case _            => name
      }
    }))
  }
}

object Age extends Column[Person] {
  def apply = { case Some(Person(_, _, age)) => Some(BigDecimalCell(age)) }
}

case class Averaged[F](column: Column[F]) extends Aggregated[F, BigDecimalCell] {
  def fun = {
    case cells => {
      val bdcs: List[BigDecimalCell] = cells.flatMap {
        case (_, bdc @ BigDecimalCell(Some(_), _, _, _)) => bdc :: Nil
        case _ => Nil
      }
      Some(BigDecimalCell(Some(bdcs.flatMap(_.scaled).sum / bdcs.size)))
    }
  }
}

class TableModelSpec extends Specification {
  "a table model" should {
    "create tables" in {
        def mkage(n: Int) = (n + 7) * 30 / 4
      val people = (0 to 9).toList.map {
        n => Person("mR. %d".format(n), "bOvEy %d".format(n), age = mkage(n))
      }
      val table = TableModel(List(FirstName |> Capitalize, LastName |> Capitalize, Averaged(Age))).table(people)
      println(table.asCSV)
      table.rows mustNot beEmpty
      for ((row, idx) <- table.rows.zipWithIndex) {
        row.columns.size must_== 3
        row.columns match {
          case StringCell(first) :: StringCell(last) :: BigDecimalCell(Some(age), _, _, _) :: Nil => {
            first must_== "Mr. %d".format(idx)
            last must_== "Bovey %d".format(idx)
            age must_== mkage(idx)
          }
          case _ => fail
        }
      }
      table.footer.flatMap(_.columns.lastOption) must beSome[Cell].which {
        case BigDecimalCell(Some(avg), _, _, _) => avg must_== people.map(_.age).sum / people.size
      }
    }
  }
}
