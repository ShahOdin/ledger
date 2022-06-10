import Domain.*
import Api.*

import scala.annotation.tailrec
import scala.collection.immutable.HashSet


trait Ledger:
  def deduceActiveTokens(pastEvents: PastEvents): ActiveTokens

object Ledger:

  @tailrec
  def constructActiveTokenLog(unprocessedPastEvents: PastEvents, log: ActiveTokenLog, nextCreatedEventChronIndex: ChronologicalIndex): ActiveTokenLog = unprocessedPastEvents match {
    case Seq() =>
      log
    case Event.Created(tokenId) +: events =>
      constructActiveTokenLog(
        unprocessedPastEvents = PastEvents.unsafe(events: _*),
        log = log.withNewToken(tokenId, nextCreatedEventChronIndex),
        nextCreatedEventChronIndex = nextCreatedEventChronIndex.next
      )
    case Event.Used(tokenId, consumed, consequences) +: events =>
      constructActiveTokenLog(
        unprocessedPastEvents = PastEvents.unsafe(consequences ++ events: _*),
        log = if(consumed) log.markAsConsumed(tokenId) else log,
        nextCreatedEventChronIndex = nextCreatedEventChronIndex.next
      )
  }

  def recursive: Ledger = { pastEvents =>
    constructActiveTokenLog(unprocessedPastEvents = pastEvents, log = ActiveTokenLog.emptyHashMap, nextCreatedEventChronIndex = ChronologicalIndex.min)
      .extractActiveTokens
  }
