package concrete

object Peano {

  sealed trait Nat
  case object Zero extends Nat
  case class Succ(pred: Nat) extends Nat

   def safeDiv(m: Nat, n: Succ): (Nat, Nat) = {
    def sdiv(div: Nat, rem: Nat): (Nat, Nat) =
      if (lessOrEq(rem, n)) (div, rem)
      else sdiv(Succ(div), minus(rem, n))
    sdiv(Zero, m)
  }

  private def lessOrEq(a: Nat, b: Nat): Boolean = (a, b) match {
    case (Succ(a1), Succ(b1)) => lessOrEq(a1, b1)
    case (Zero, _) => true
    case _ => false
  }

  // assumes a >= b
  private def minus(a: Nat, b: Nat): Nat = (a, b) match {
    case (Succ(a1), Succ(b1)) => minus(a1, b1)
    case _ => a
  }
}
