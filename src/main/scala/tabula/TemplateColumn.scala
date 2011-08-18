package tabula

case class TemplateColumn(template: () => String, attrs: () => Map[String, Any]) extends Column {
  def format: String =
    throw new UnsupportedOperationException("format on TemplateColumn doesn't make sense")
}
