package tabula.test

import tabula._
import Tabula._
import org.scala_tools.time.Imports._
import shapeless._
import shapeless.HList._
import shapeless.Poly._
import shapeless.Functions._
import shapeless.TypeOperators._
import shapeless.Mapper._
import shapeless.MapperAux._
import shapeless.ZipApply._
import shapeless.ZipApplyAux._
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
  val row = rm.row(p)
}

class TwoSpec extends Specification {
  "safety" should {
    "be first" in {
      import TwoSpec._
      println(rm)
      println(row.cells)
    }
  }
}
