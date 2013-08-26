package tabula.test

import tabula._
import Tabula._
import tabula.util._
import shapeless._
import shapeless.HList._
import org.specs._
import com.github.nscala_time.time.Imports._
import org.apache.commons.lang3.text.WordUtils.capitalize

// a pretend data model

case class UselessItem(name: String, price: Double)
case class PretentiousPurveyor(name: String, location: String)
case class Purchase(item: UselessItem, date: Option[DateTime], from: PretentiousPurveyor)

// some test data

object Items {
  val justSomeCoatRack = UselessItem("honest abe", 90.39)
  val cheeseParkingSpot = UselessItem("fancy cheese board", 39.95)
  val whatIsThis = UselessItem("faux professional tool pouch", 48.00)
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
object ItemName extends Column((_: Purchase).item.name)

// how much we paid
object ItemPrice extends Column((_: Purchase).item.price)

// where we bought it
object PurchaseLocation extends Column((_: Purchase).from.location)

// date of purchase
object DateOfPurchase extends Column((_: Purchase).date)

// transformer column: capitalize words
object Capitalize extends Column(capitalize)

object TotalPaid extends Fold(ItemPrice)(0)(_ + _)

// unlimited extensibility via type classes!
object Extensibility {
  import scala.xml._

  // we'll output cells of type Cell[NodeSeq] via a function that
  // procudes objects of type HTML
  case class HTML(nodes: NodeSeq)

  // provide a way of lazily converting HTML => Cell[NodeSeq]
  implicit object HTMLNodeSeqCellulizer extends Cellulizer[HTML, NodeSeq](_.nodes)

  // here's a column that produces HTML from Purchase-s
  object Title extends Column((p: Purchase) => HTML(<title>{ p.item.name }</title>))

  // create custom CSV output that overrides some default formats and
  // implements conversion of NodeSeq-s to text (which is what CSV
  // ultimately is)
  object MyCSV extends CSV {
    implicit val BigDecimalFormatter = new BigDecimalFormatter(new java.text.DecimalFormat("#,##0.00000;-#,##0.00000"))
    implicit val DateTimeFormatter = new DateTimeFormatter(org.joda.time.format.DateTimeFormat.forPattern("dd MMM yyyy"))
    implicit object NodeSeqFormatter extends SimpleFormatter[NodeSeq] {
      def apply(cell: Cell[NodeSeq]) = StringFormatter.quote(cell.value.map(_ \\ "title").map(_.toString))
    }
  }
}

import Extensibility._

object ShowcaseSpec {
  val columns =
    (ItemName | Capitalize) @@ "Item Name" ::
      ItemPrice @@ "Item Price" ::
      PurchaseLocation @@ "Bought At" ::
      DateOfPurchase @@ "Date of Purchase" ::
      Title ::
      HNil

  val cellsF = cells(columns)
}

// let's do it!
class ShowcaseSpec extends Specification {
  "a purchase history" should {
    import ShowcaseSpec._
    "print out a list of things we've bought" in {
      for (purchase <- Purchases.*) {
        val row = cellsF(purchase).row(MyCSV)
        println(row)
      }
    }
  }
}
