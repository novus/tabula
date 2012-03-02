package tabula.test

import tabula._
import Tabula._
import org.specs._
import org.scala_tools.time.Imports._

// a pretend data model

case class UselessItem(name: String, price: Double)
case class PretentiousPurveyor(name: String, location: String)
case class Purchase(item: UselessItem, date: Option[DateTime], from: PretentiousPurveyor)

// some test data

object Items {
  val justSomeCoatRack = UselessItem("Honest Abe", 90.00)
  val cheeseParkingSpot = UselessItem("Fancy Cheese Board", 30.00)
  val whatIsThis = UselessItem("Faux Professional Tool Pouch", 48.00)
}

object PlacesNormalPeopleDoNotGo {
  val SchizophrenicMonkey = PretentiousPurveyor("Tinkering Monkey", "SF Bay Area")
  val BrooklynSlateWtf = PretentiousPurveyor("Brooklyn Slate Co.", "Brokelyn")
  val HeritageInsanityCo = PretentiousPurveyor("Heritage Leather Co.", "Somewhere in Cali")
}

object Purchases {
  import Items._
  import PlacesNormalPeopleDoNotGo._

  val * = {
    Purchase(item = justSomeCoatRack, date = Some(DateTime.now), from = SchizophrenicMonkey) ::
      Purchase(item = cheeseParkingSpot, date = None, from = BrooklynSlateWtf) ::
      Purchase(item = whatIsThis, date = Some(DateTime.now), from = HeritageInsanityCo) ::
      Nil
  }
}

// column descriptions

// what we bought
object ItemName extends Column[Purchase, String](_.item.name)

// how much we paid
object ItemPrice extends Column[Purchase, BigDecimal](_.item.price)

// where we bought it
object PurchaseLocation extends Column[Purchase, String](_.from.location)

// date of purchase
object DateOfPurchase extends Column[Purchase, String](_.date.map("%s".format(_)).getOrElse("N/A"))

object ShowcaseSpec {
  // tell TableModel which columns to use when making a Table
  val model = {
    "Item Name" -> ItemName &
      "Item Price" -> ItemPrice &
      "Bought At" -> PurchaseLocation &
      "Date of Purchase" -> DateOfPurchase
  }

  // produce List[Row] from a List[Purchase]
  val rows = model(Purchases.*)
}

// let's do it!
class ShowcaseSpec extends Specification {
  "a purchase history" should {
    "print out a list of things we've bought" in {
      import ShowcaseSpec._
      for (row <- rows)
        println(row.cells.flatMap(_.value).mkString(" | "))
    }
  }
}
