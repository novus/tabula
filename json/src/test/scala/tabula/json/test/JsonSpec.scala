package tabula.json.test

import tabula._
import Tabula._
import tabula.json._
import tabula.test._
import org.specs._
import org.json4s._
import org.json4s.native.JsonMethods._
import shapeless.HList._

class JsonSpec extends Specification {
  import ShowcaseSpec._
  "a JSON output" should {
    "produce JSON" in {
      val jos = Purchases.*.map(purchase => JObject(rowF(purchase).map(JSON).toList))
      println(jos)
    }
  }
}
