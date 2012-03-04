package tabula.test

import tabula._
import Tabula._
import tabula.util._
import org.specs._
import org.scala_tools.time.Imports._

// a pretend data model

case class UselessItem(name: String, price: Double)
case class PretentiousPurveyor(name: String, location: String)
case class Purchase(item: UselessItem, date: Option[DateTime], from: PretentiousPurveyor)

// some test data

object Items {
  val justSomeCoatRack = UselessItem("Honest Abe", 90.39)
  val cheeseParkingSpot = UselessItem("Fancy Cheese Board", 39.95)
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
object ItemName extends Column((p: Purchase) => p.item.name)

// how much we paid
object ItemPrice extends Column((p: Purchase) => p.item.price)

// where we bought it
object PurchaseLocation extends Column((p: Purchase) => p.from.location)

// date of purchase
object DateOfPurchase extends Column((p: Purchase) => p.date)

object ShowcaseSpec {
  // tell TableModel which columns to use when making a Table's rows
  val row = {
    "Item Name" -> ItemName &
      "Item Price" -> ItemPrice &
      "Bought At" -> PurchaseLocation &
      "Date of Purchase" -> DateOfPurchase
  }

  object TotalPaid extends Fold(ItemPrice)(0)(_ + _)

  val model = TableModel(row, agg = Map(TotalPaid))

  // produce List[Row] from a List[Purchase]
  val table = model(Purchases.*)
}

// let's do it!
class ShowcaseSpec extends Specification {
  "a purchase history" should {
    "print out a list of things we've bought" in {
      import ShowcaseSpec._
      println(CSV(table))
    }
  }
}
