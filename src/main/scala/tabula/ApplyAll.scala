package tabula

import shapeless._
import Tabula._

// shamelessly stolen from https://gist.github.com/travisbrown/5124684

trait ApplyAll[A, L <: HList, Out <: HList] extends ((A, L) => Out)

object ApplyAll {
  implicit def hnilApplyAll[A] = new ApplyAll[A, HNil, HNil] {
    def apply(a: A, l: HNil) = HNil
  }

  implicit def hlistApplyAll[A, R, L <: HList, O <: HList](implicit aa: ApplyAll[A, L, O]) =
    new ApplyAll[A, (A => R) :: L, R :: O] {
      def apply(a: A, l: (A => R) :: L) = l.head(a) :: aa(a, l.tail)
    }

  implicit def hlistNamedColumnApplyAll[F, T, C, L <: HList, O <: HList](implicit aa: ApplyAll[F, L, O]) =
    new ApplyAll[F, NamedColumn[F, T, C]:: L, ColumnAndCell[F, T, C]:: O] {
      def apply(a: F, l: NamedColumn[F, T, C] :: L) = l.head(a) :: aa(a, l.tail)
    }

  def apply[A, L <: HList, O <: HList](a: A)(l: L)(implicit aa: ApplyAll[A, L, O]) =
    aa(a, l)
}
