package abstracted.additionalextractor

import scala.language.implicitConversions

// Implemented as only integers using only 0 and positive integers
object IntNums extends Peano {
  type Nat  = Int
  type Zero = Int
  type Succ = Int

  val Zero: Zero = 0

  object Succ extends SuccExtractor {
    def apply(nat: Nat): Succ = nat + 1
    def unapply(nat: Nat): Option[Nat] =
      if (nat > 0) Some(nat - 1) else None
  }

  def succDeco(succ: Succ): SuccAPI = new SuccAPI {
    def pred: Nat = succ - 1
  }

  val SuccId: SuccIdExtractor = new SuccIdExtractor {
    def unapply(nat: Nat): Option[Succ] =
      if (nat > 0) Some(nat) else None
  }

  def safeDiv(m: Nat, n: Succ): (Nat, Nat) = (m / n, m % n)

}