import Domain.*
import Api.*

object Util:

  final case class PastEventsGenSummary (
                                                 activeTokens: Set[TokenId],
                                                 history: PastEvents,
                                                 createdEventChance: Double,
                                                 remainingEventCountAllowed: Int,
                                             ){
    def append(event: Event): PastEventsGenSummary = {
      val newActiveTokens: Set[TokenId] = event match {
        case Event.Created(_) => activeTokens.incl(event.tokenId)
        case Event.Used(tokenId, consumed, _) => if(consumed) activeTokens.excl(tokenId) else activeTokens
      }

      this.copy(
        remainingEventCountAllowed = remainingEventCountAllowed - 1,
        history = PastEvents.unsafe(history :+ event: _*),
        activeTokens = newActiveTokens
      )
    }

    def transformedForConsequences(tokenId: TokenId, consumed: Boolean, consequencesLength: Int): PastEventsGenSummary = this.copy(
      activeTokens = if(consumed) activeTokens.excl(tokenId) else activeTokens,
      remainingEventCountAllowed = consequencesLength,
      history = PastEvents.empty
    )
    
  }

  object PastEventsGenSummary:
    def apply(maxEventCount: Int, createdEventChance: Double): PastEventsGenSummary = new PastEventsGenSummary(
      activeTokens = Set.empty,
      history = PastEvents.empty,
      remainingEventCountAllowed = maxEventCount,
      createdEventChance = createdEventChance
    )
