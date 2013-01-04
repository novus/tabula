package tabula.test

import tabula._
import Tabula._
import com.github.nscala_time.time.Imports._
import shapeless._
import shapeless.HList._
import org.specs._

case class Person(name: String, age: Int, timestamp: DateTime)
object Name extends Column((p: Person) => "name is '%s'".format(p.name))
object Age extends Column((p: Person) => p.age)
object Timestamp extends Column((p: Person) => p.timestamp)
object TwoSpec {
  val n = "NAME" -> Name
  val a = "AGE" -> Age
  val t = "TIMESTAMP" -> Timestamp
  val p = Person("max", 26, DateTime.now)
  val rm = RowModel(n |: a |: t)
  val row = rm.Row(p)
}

class TwoSpec extends Specification {
  "safety" should {
    "be first" in {
      import TwoSpec._
      println(rm)
      println(row.cells)
      println(row.cells.map(CSV).toList.mkString(","))
    }
  }
}
