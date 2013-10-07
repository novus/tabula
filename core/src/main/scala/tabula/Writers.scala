package tabula

import shapeless._
import java.io.{ File, OutputStream }

trait Writers {
  self: Format =>

  abstract class Writer[In](in: In) {
    def before() {}
    def write(rows: Iterator[Row]): Unit
    def after() {}
  }

  abstract class WriterSpawn(protected val names: List[Option[String]]) {
    def toStream(out: OutputStream): Writer[OutputStream]
    def toFile(file: java.io.File) =
      toStream(new java.io.FileOutputStream(file)).asInstanceOf[Writer[java.io.File]]
    def toConsole() = toStream(System.out)
  }

  def writer[F, T, C, NcT <: HList, Col](cols: Col :: NcT)(implicit ev: Col <:< Column[F, T, C], tl: ToList[Col :: NcT, Column[_, _, _]]): WriterSpawn
}
