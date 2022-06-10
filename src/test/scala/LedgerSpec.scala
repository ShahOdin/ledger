import Api.*
import Domain.*
import Generators.*
import org.scalacheck.Prop

class LedgerSpec extends munit.FunSuite:

  val inefficientLedger: Ledger = {
    import Ops.*
    events =>
      ActiveTokens.of(events.activeTokenIds)
  }

  val efficientLedger: Ledger = Ledger.recursive

  test("The inefficient impl should return only the active token ids."){
    val obtained = inefficientLedger.deduceActiveTokens(TestData.events)
    val expected = ActiveTokens.of(Seq("b", "d", "e").map(TokenId.of))

    assertEquals(obtained, expected)
  }


  test("The efficient impl should return only the active token ids."){
    val obtained = efficientLedger.deduceActiveTokens(TestData.events)
    val expected = ActiveTokens.of(Seq("b", "d", "e").map(TokenId.of))

    assertEquals(obtained, expected)
  }
