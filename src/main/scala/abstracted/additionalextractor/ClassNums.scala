package abstracted.additionalextractor

sealed trait NatTrait
object ZeroObject extends NatTrait {
  override def toString(): String = "ZeroObject"
}
final class SuccClass(val predecessor: NatTrait) extends NatTrait {
  override def toString(): String = s"SuccClass($predecessor)"
}

object ClassNums extends Peano {

  type Nat  = NatTrait
  type Zero = ZeroObject.type
  type Succ = SuccClass

  val Zero: Zero = ZeroObject

  object Succ extends SuccExtractor {
    def apply(nat: Nat): Succ = new SuccClass(nat)
    def unapply(nat: Nat): Option[Nat] = nat match {
      case nat: SuccClass => Some(nat.predecessor)
      case _ => None
    }
  }

  val SuccId: SuccIdExtractor = new SuccIdExtractor {
    def unapply(nat: Nat): Option[Succ] = nat match {
      case nat: SuccClass => Some(nat)
      case _ => None
    }
  }

  def succDeco(succ: Succ): SuccAPI = new SuccAPI {
    def pred: Nat = succ.predecessor
  }

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
