package abstracted.refinedscrutinee

import scala.language.implicitConversions

import scala.reflect.ClassTag

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
    // !!!!!!!!!!!!!!!!!!!!
    // We add the refinement `{ def refined: nat.type }` to ensure that the implementation of refined returns the actual scrutenee
    // Note that the implementation will have type a type `SuccOpt { def refined: Succ & nat.type }` or more precise.
    // The compiler should warn (or emmit an error) if this constraint is not present.
    // !!!!!!!!!!!!!!!!!!!!
    def unapply(nat: Nat): SuccOpt { type Scrutinee <: nat.type }
  }
  trait SuccOpt {
    type Scrutinee <: Singleton
    def refinedScrutinee: Succ & Scrutinee
    def isEmpty: Boolean // did the match fail?
    def get: Nat // get pred
  }

  def safeDiv(m: Nat, n: Succ): (Nat, Nat)
}
