package abstracted.refinedscrutinee2

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
    // We add the Scrutinee type nat.type to ensure that the implementation of refined returns the actual scrutenee
    // Note that the implementation will have type a type `SuccOpt[nat.type]` or more precise.
    // The compiler should warn (or emmit an error) if this constraint on `nat.type` is not present.
    // !!!!!!!!!!!!!!!!!!!!
    def unapply(nat: Nat): SuccOpt[nat.type]
  }
  trait SuccOpt[+Scrutinee <: Singleton] {
    def refinedScrutinee: Succ & Scrutinee
    def isEmpty: Boolean // did the match fail?
    def get: Nat // get pred
  }

  def safeDiv(m: Nat, n: Succ): (Nat, Nat)
}
