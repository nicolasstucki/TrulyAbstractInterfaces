import org.junit.Test

class TestMains {
  @Test def test: Unit = {
    // Test that Apps run without crashing
    concrete.App.main(Array())
    abstracted.withcast.App.main(Array())
    abstracted.additionalextractor.App.main(Array())
    abstracted.classtags.App.main(Array())
    abstracted.refinedscrutinee.App.main(Array())
    abstracted.refinedscrutinee2.App.main(Array())
  }
}
