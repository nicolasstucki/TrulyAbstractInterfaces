package abstracted.classtags

object AppBug {
  def main(args: Array[String]): Unit = {
    val peano: Peano = IntNumsBuggy
    import peano._

    def match1(n: Nat) = n match {
      case n: Zero => println(s"$n matched Zero")
      case n: Succ => println(s"$n matched Succ")
    }

    def match2(n: Nat) = n match {
      case n: Succ => println(s"$n matched Succ")
      case n: Zero => println(s"$n matched Zero")
    }

    match1(Zero)
    match1(Succ(Zero))

    match2(Zero)
    match2(Succ(Zero))
  }
}
