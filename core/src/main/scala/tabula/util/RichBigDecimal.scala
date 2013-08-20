package tabula.util

case class RichBigDecimal(private[util] val value: Option[BigDecimal]) {
  lazy val value_! = value.getOrElse(NakedZero)
  lazy val asDouble = value.map(_.doubleValue)
  lazy val asDouble_! = value_!.doubleValue
  def +(right: RichBigDecimal) = copy(value.map(_ + right.value_!))
  def -(right: RichBigDecimal) = copy(value.map(_ - right.value_!))
  def *(right: RichBigDecimal) = copy(value.map(_ * right.value_!))
  def /(right: RichBigDecimal) = copy(value.map(_ / right.value_!))
}
