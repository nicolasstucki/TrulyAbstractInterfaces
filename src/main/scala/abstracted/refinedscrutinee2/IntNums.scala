package abstracted.refinedscrutinee2

import scala.language.implicitConversions

import scala.reflect.ClassTag

// Implemented as only integers using only 0 and positive integers
object IntNums extends Peano {
  type Nat  = Int
  type Zero = Int
  type Succ = Int

  val Zero: Zero = 0

  object Succ extends SuccExtractor {
    def apply(nat: Nat): Succ = nat + 1
    def unapply(nat: Nat) = new SuccOpt[nat.type] {
      def refinedScrutinee: Succ & nat.type = nat
      def isEmpty: Boolean = nat == 0
      def get: Nat = nat - 1
    }
  }

  def succDeco(succ: Succ): SuccAPI = new SuccAPI {
    def pred: Nat = succ - 1
  }

  def safeDiv(m: Nat, n: Succ): (Nat, Nat) = (m / n, m % n)

}