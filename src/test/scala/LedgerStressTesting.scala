import Api.*
import Domain.*
import Generators.*
import org.scalacheck.Prop

class LedgerStressTesting extends munit.ScalaCheckSuite:

  val inefficientLedger: Ledger = {
    import Ops.*
    events => ActiveTokens.of(events.activeTokenIds)
  }
  val efficientLedger: Ledger = Ledger.recursive

  property("For an arbitrary set of events, the efficient and non efficient impls should return the same result."){
    Prop.forAll(Generators.pastEventsGen(maxEventCount = 10, createdEventChance = 0.5)){ pastEvents =>
      val obtained: ActiveTokens = efficientLedger.deduceActiveTokens(pastEvents)
      val expected: ActiveTokens = inefficientLedger.deduceActiveTokens(pastEvents)

      assertEquals(obtained, expected)
    }
  }

  property("For repeated set of created events, the efficient and non efficient impls should return the same result."){
    Prop.forAll(Generators.pastEventsGen(maxEventCount = 10, createdEventChance = 1)){ pastEvents =>
      val obtained: ActiveTokens = efficientLedger.deduceActiveTokens(pastEvents)
      val expected: ActiveTokens = inefficientLedger.deduceActiveTokens(pastEvents)

      assertEquals(obtained, expected)
    }
  }