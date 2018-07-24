package concrete

object App {
  def main(args: Array[String]): Unit = {
    import Peano._

    def divOpt(m: Nat, n: Nat): Option[(Nat, Nat)] = {
      n match {
        case Zero => None
        case s @ Succ(_) => Some(safeDiv(m, s))
      }
    }

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
