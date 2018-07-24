package abstracted.refinedscrutinee2

object App {
  def main(args: Array[String]): Unit = {
    println("------------------------------------")
    println("Testing PeanoConsumer with ClassNums")
    println("------------------------------------")
    new PeanoConsumer { val peano = ClassNums }.runApp()

    println()
    println()

    println("----------------------------------")
    println("Testing PeanoConsumer with IntNums")
    println("----------------------------------")
    new PeanoConsumer { val peano = IntNums }.runApp()
  }
}

trait PeanoConsumer {
  val peano: Peano
  import peano._ // import Nat, Zero, Succ, extractors and so on

  def divOpt(m: Nat, n: Nat): Option[(Nat, Nat)] = {
    n match {
      case Zero => None
      // case s @ Succ(p) => Some(safeDiv(m, s))
      case _ =>
        // !!!!!!!!!!!!!!!!!!!
        // case s @ Succ(p) => Some(safeDiv(m, s))
        // which would be be compiled as
        // !!!!!!!!!!!!!!!!!!!
        val x1 = Succ.unapply(n)
        if (!x1.isEmpty) {
          val s: n.type & Succ = x1.refinedScrutinee
          val p: Nat = x1.get
          Some(safeDiv(m, s))
        } else {
          // Falthough next branch, in this case a MatchError
          throw new MatchError(n)
        }
        // !!!!!!!!!!!!!!!!!!!
    }
  }

  def runApp(): Unit = {
    val one = Succ(Zero)
    val two = Succ(one)
    val three = Succ(two)

    println("safeDiv")
    println("2 / 3 = " + safeDiv(two, three))
    println("3 / 2 = " + safeDiv(three, two))
    // println("2 / 0 = " + safeDiv(two, Zero)) // Does not compile (as expected)

    println()

    println("divOpt")
    println("2 / 3 = " + divOpt(two, three))
    println("3 / 2 = " + divOpt(three, two))
    println("2 / 0 = " + divOpt(two, Zero))
  }
}