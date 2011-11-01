package tabula

trait Column[T] {
  def apply(x: T): Option[Cell]
  def name: String
  def label: String
}
