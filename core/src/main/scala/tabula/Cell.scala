package tabula

/** General-purpose cell abstraction that ships a potentially missing
  * value of a table cell. Cell[A] serves as a lingua franca for all
  * Tabula components downstream of [[tabula.Column]].
  */
trait Cell[A] {
  cell =>

  /** The table cell's value. Since all cell values are nullable, all
    * consumers must be able to handle missing values.
    */
  def value: Option[A]

  /** Manifest describing A. */
  def manifest: Manifest[A]

  /** Transforms Cell[A] to Cell[B].  Missing A will result in missing
    * B. Use [[flatMap]] to coerce a present A into a (potentially)
    * missing B.
    */
  def map[B: Manifest](f: A => B): Cell[B] = new Cell[B] {
    def manifest = implicitly[Manifest[B]]
    lazy val value = cell.value.map(f)
  }

  /** Transforms Cell[A] to Cell[B]. Missing A will result in missing
    * B. At its discretion, `f` may decide to drop the B by returning
    * None.
    */
  def flatMap[B: Manifest](f: A => Option[B]): Cell[B] = new Cell[B] {
    def manifest = implicitly[Manifest[B]]
    lazy val value = cell.value.flatMap(f)
  }
}
