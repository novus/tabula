package tabula

package object util {
  object NakedZero extends BigDecimal(new java.math.BigDecimal(0))
  val Zero = Option(NakedZero)
  implicit def enrichBigDecimal(bd: BigDecimal): RichBigDecimal =
    RichBigDecimal(Option(bd))
  implicit def enrichOptBigDecimal(obd: Option[BigDecimal]): RichBigDecimal =
    RichBigDecimal(obd)
  implicit def unwrapBigDecimal(rbd: RichBigDecimal): BigDecimal = rbd.value_!
  implicit def unwrapOptBigDecimal(rbd: RichBigDecimal): Option[BigDecimal] = rbd.value
  implicit def unwrapDouble(rbd: RichBigDecimal): Double = rbd.asDouble_!
  implicit def unwrapOptDouble(rbd: RichBigDecimal): Option[Double] = rbd.asDouble
}
