package tabula.test

import tabula._
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

object PlacesNormalPeopleOoNotGo {
  val SchizophrenicMonkey = PretentiousPurveyor("Tinkering Monkey", "SF Bay Area")
  val BrooklynSlateWtf = PretentiousPurveyor("Brooklyn Slate Co.", "Brokelyn")
  val HeritageInsanityCo = PretentiousPurveyor("Heritage Leather Co.", "Somewhere in Cali")
}

object Purchases {
  import Items._
  import PlacesNormalPeopleOoNotGo._

  val * = {
    Purchase(item = justSomeCoatRack, date = Some(DateTime.now), from = SchizophrenicMonkey) ::
      Purchase(item = cheeseParkingSpot, date = None, from = BrooklynSlateWtf) ::
      Purchase(item = whatIsThis, date = Some(DateTime.now), from = HeritageInsanityCo) ::
      Nil
  }
}

// column descriptions

// what we bought
object ItemName extends Column[Purchase] {
  def cell = { case Some(Purchase(UselessItem(name, _), _, _)) => Some(StringCell(name)) }
}

// how much we paid
object ItemPrice extends Column[Purchase] {
  def cell = { case Some(Purchase(UselessItem(_, price), _, _)) => Some(BigDecimalCell(Some(BigDecimal(price)))) }
}

object PriceAsWholeDollars extends Column[BigDecimalCell] {
  def cell = {
    case Some(bdc @ BigDecimalCell(Some(_), _, _, _)) =>
      Some(StringCell("$%s".format(bdc.copy(formatter = BigDecimalCell.Whole).human)))
  }
}

// where we bought it
object PurchaseLocation extends Column[Purchase] {
  def cell = { case Some(Purchase(_, _, PretentiousPurveyor(_, location))) => Some(StringCell(location)) }
}

// date of purchase
object DateOfPurchase extends Column[Purchase] {
  def cell = { case Some(Purchase(_, date, _)) => Some(DateTimeCell(date)) }
}

object HumanReadableDate extends Column[DateTimeCell] {
  import org.scala_tools.time.Imports._
  private lazy val formatter = Some(DateTimeFormat.forPattern("MMMM d, YYYY"))
  def cell = { case Some(dtc @ DateTimeCell(_, _)) => Some(dtc.copy(formatter = formatter)) }
}

object ShowcaseSpec {
  // define some column chains, | means "pipe"
  val price = ItemPrice | PriceAsWholeDollars
  val date = DateOfPurchase | HumanReadableDate

  // tell TableModel which columns to use when making a Table
  val model = TableModel(ItemName :: price :: PurchaseLocation :: date :: Nil)

  // produce Table from a List[Purchase]
  val table = model.table(Purchases.*)
}

// let's do it!
class ShowcaseSpec extends Specification {
  "a purchase history" should {
    "print out a list of things we've bought" in {
      import ShowcaseSpec._

      // show me the monay!
      println(CSV(table))
    }
  }
}
