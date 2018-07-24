package abstracted.classtags

import scala.language.implicitConversions

import scala.reflect.ClassTag

// Implemented as only integers using only 0 and positive integers
object IntNumsBuggy extends Peano {
  type Nat  = Int
  type Zero = Int
  type Succ = Int

  def natTag: ClassTag[Nat] = ClassTag(classOf[Int])
  def zeroTag: ClassTag[Zero] = ClassTag(classOf[Int])
  def succTag: ClassTag[Succ] = ClassTag(classOf[Int])

  val Zero: Zero = 0

  object Succ extends SuccExtractor {
    def apply(nat: Nat): Succ = nat + 1
    def unapply(nat: Succ): Option[Nat] =
      if (nat > 0) Some(nat - 1) else None
  }

  def succDeco(succ: Succ): SuccAPI = new SuccAPI {
    def pred: Nat = succ - 1
  }

  def safeDiv(m: Nat, n: Succ): (Nat, Nat) = (m / n, m % n)

}