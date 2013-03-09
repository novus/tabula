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
  val columns = "NAME" -> Name |: "AGE" -> Age |: "TIMESTAMP" -> Timestamp
  val person = Person("max", 26, DateTime.now)
  val rowF = row(columns) _
}

class TwoSpec extends Specification {
  "safety" should {
    "be first" in {
      import TwoSpec._
      println(rowF(person))
      println(rowF(person).map(CSV).toList.mkString(","))
    }
  }
}
