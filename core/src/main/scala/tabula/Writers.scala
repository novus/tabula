package tabula

import shapeless._
import shapeless.ops.hlist._
import java.io.{ File, OutputStream, FileOutputStream }

trait Writers {
  self: Format =>

  abstract class Writer {
    def start() {}
    def writeMore(rows: Iterator[Row]): Unit
    def write(rows: Iterator[Row]) {
      start()
      writeMore(rows)
      finish()
    }
    def finish() {}
  }

  abstract class WriterSpawn(protected val names: List[Option[String]]) {
    def toStream(out: OutputStream): Writer
    def toFile(file: File) = toStream(new FileOutputStream(file))
    def toConsole() = toStream(System.out)
  }

  type Spawn <: WriterSpawn

  def writer[F, T, C, NcT <: HList, Col](cols: Col :: NcT)(implicit ev: Col <:< Column[F, T, C], tl: ToList[Col :: NcT, Column[_, _, _]]): Spawn = writer(NamedColumn.names(cols))
  def writer(names: List[Option[String]]): Spawn
}
