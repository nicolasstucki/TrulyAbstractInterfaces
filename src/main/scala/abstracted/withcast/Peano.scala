package abstracted.withcast

import scala.language.implicitConversions

trait Peano {
  type Nat
  type Zero <: Nat
  type Succ <: Nat

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
    def unapply(nat: Nat): Option[Nat]
  }

  def safeDiv(m: Nat, n: Succ): (Nat, Nat)
}
