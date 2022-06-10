import Domain.*
import Api.*
import Ops.*

class EventOpsSpec extends munit.FunSuite:

  test("extractAllTokenIds should preserve the order of events."){
    val obtained = TestData.events.tokenIdsSortedByCreation
    val expected = Seq(
      "a",
      "b",
      "c",
      "d",
      "e"
    ).map(TokenId.of)

    assertEquals(obtained, expected)
  }

  test("archivedTokenIds should return only the archived token ids."){
    val obtained = TestData.events.archivedTokenIds
    val expected = Set(
      "a",
      "c"
    ).map(TokenId.of)

    assertEquals(obtained, expected)
  }

  test("activeTokenIds should return only the active token ids."){
    val obtained = TestData.events.activeTokenIds
    val expected = Seq(
      "b",
      "d",
      "e"
    ).map(TokenId.of)

    assertEquals(obtained, expected)
  }
