package abstracted.classtags

import scala.language.implicitConversions

import scala.reflect.ClassTag

trait Peano {
  type Nat
  type Zero <: Nat
  type Succ <: Nat

  implicit def natTag: ClassTag[Nat]
  implicit def zeroTag: ClassTag[Zero]
  implicit def succTag: ClassTag[Succ]

  // add members via extension methods
  implicit def succDeco(succ: Succ): SuccAPI
  trait SuccAPI {
    def pred: Nat
  }

  // expose support for pattern matching via extractors:
  val Zero: Zero
  val Succ: SuccExtractor
  trait SuccExtractor {
    def apply(nat: Nat): Succ
    def unapply(nat: Succ): Option[Nat]
  }

  def safeDiv(m: Nat, n: Succ): (Nat, Nat)
}
