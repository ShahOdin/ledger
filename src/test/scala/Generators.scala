import Api.*
import Domain.*
import Util.PastEventsGenSummary
import org.scalacheck.Gen

import scala.util.Random

object Generators:

  val tokenIdGen: Gen[TokenId] = Gen
    .uuid
    .map(_.toString)
    .map(TokenId.of)

  val createdEventGen: Gen[Event.Created] = tokenIdGen
    .map(Event.Created.apply)

  private def addEvent(summary: PastEventsGenSummary): Gen[PastEventsGenSummary] = if(summary.remainingEventCountAllowed > 0){
    Gen
      .prob(summary.createdEventChance)
      .map(_ || summary.activeTokens.isEmpty)
      .flatMap{
        case true =>
          addCreatedEvent(summary).flatMap(addEvent)
        case false =>
          addUsedEvent(summary).flatMap(addEvent)
      }
  } else Gen.const(summary)

  private def addCreatedEvent(summary: PastEventsGenSummary): Gen[PastEventsGenSummary] = createdEventGen
    .map(summary.append)

  private def addConsequencesToUsedEvent(summary: PastEventsGenSummary, tokenToBeUsed: TokenId, consumed: Boolean): Gen[PastEventsGenSummary] = for {
    consequencesLength <- Gen.chooseNum(0, summary.remainingEventCountAllowed)
    transformedSummary = summary.transformedForConsequences(tokenToBeUsed, consumed, consequencesLength)
    consequencesAdded <- addEvent(transformedSummary)
  } yield consequencesAdded

  private def addUsedEvent(summary: PastEventsGenSummary): Gen[PastEventsGenSummary] = for {
    tokenToBeUsed <- Gen.oneOf(summary.activeTokens)
    consumed <- Gen.prob(summary.createdEventChance)
    consequences <- addConsequencesToUsedEvent(summary, tokenToBeUsed, consumed)
    usedEvent = Event.Used(
      tokenId = tokenToBeUsed,
      consumed = consumed,
      consequences = consequences.history
    )
    updatedSummary = summary.append(usedEvent)
  } yield updatedSummary.copy(remainingEventCountAllowed = consequences.remainingEventCountAllowed)

  def pastEventsGen(maxEventCount: Int, createdEventChance: Double): Gen[PastEvents] = addEvent(
    PastEventsGenSummary(maxEventCount, createdEventChance)
  ).map(_.history)

