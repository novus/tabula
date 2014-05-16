package tabula

import scala.reflect.ClassTag
import scala.reflect.runtime.{ universe => ru }

case class DynamicFormat[F <: Format: ru.TypeTag: ClassTag](val fmt: F) {
  lazy val tpe = ru.typeOf[F]

  lazy val formatterSymbols: Map[String, Either[ru.MethodSymbol, ru.ModuleSymbol]] = {
    val ftpe = implicitly[ru.TypeTag[F]].tpe
      def isFormatter(tpe: ru.Type) = tpe <:< ru.typeOf[Format#Formatter[_]]
    ftpe.members
      .filter { mem => (mem.isModule || mem.isMethod) && mem.isImplicit }
      .map {
        candidate =>
          candidate.name.toString -> (if (candidate.isModule) Right(candidate.asModule)
          else if (candidate.isMethod) Left(candidate.asMethod)
          else ???)
      }.toMap.filter {
        case (name, Right(module: ru.ModuleSymbol)) =>
          module.moduleClass.typeSignature.baseClasses.filter(_.isClass).map(_.asType.toType).exists(isFormatter)
        case (name, Left(method: ru.MethodSymbol)) =>
          isFormatter(method.returnType)
      }
  }

  lazy val mirror = ru.runtimeMirror(fmt.getClass.getClassLoader).reflect(fmt)

  lazy val formatters =
    formatterSymbols.foldLeft(Map.empty[Manifest[_], (String, fmt.Formatter[_])]) {
      case (acc, (name, Right(module))) =>
        val fter = mirror.reflectModule(module).instance.asInstanceOf[fmt.Formatter[_]]
        acc + (fter.manifest -> (name, fter))
      case (acc, (_, Left(_))) => acc
    }

  class MissingFormatter[C](val cell: Cell[C]) extends IllegalArgumentException(s"unable to find formatter in '$tpe' for '${cell.manifest}'")

  def apply[C](cell: Cell[C]) = {
    formatters.get(cell.manifest) match {
      case Some((_, fter: fmt.Formatter[C])) => fter.format(cell)
      case _                                 => throw new MissingFormatter(cell)
    }
  }

  def apply(cells: List[Cell[_]]): fmt.Row =
    cells.foldLeft(fmt.RowProto.emptyRow)((row, cell) => this(cell).foldLeft(row)((r, v) => fmt.RowProto.appendBase(v)(r)))
}
