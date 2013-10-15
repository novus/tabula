package tabula.json.test

import tabula._
import Tabula._
import tabula.json._
import tabula.test._
import org.specs2.mutable._
import org.json4s._
import shapeless._
import scala.xml._

object MyJSON extends JSON {
  implicit object NodeSeqFormatter extends Formatter[NodeSeq] {
    type Local = JString
    def apply(value: Option[NodeSeq]) = JString(value.map(_ \\ "title").map(_.toString).getOrElse("")) :: Nil
  }
}

class JsonSpec extends Specification {
  import ShowcaseSpec._
  "a JSON output" should {
    "produce JSON" in {
      val writer = MyJSON.writer(columns).toConsole()
      writer.write(for (purchase <- Purchases.*.iterator) yield cellsF(purchase).row(MyJSON))
      success
    }
  }
}
