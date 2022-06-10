import scala.::
import Api.*

object Ops:
  
  /**
   * simple ops to use in a crude test implementation of Ledger. 
  **/
  extension (event: Event) {
    def flattenedOrder: Seq[Event] = event match {
      case _:Event.Created =>
        Seq(event)
      case e@Event.Used(_, _, consequences) =>
        e.copy(consequences = Seq.empty) +: consequences.flatMap(_.flattenedOrder)
    }
  }

  extension (pastEvents: PastEvents){
    def tokenIdsSortedByCreation: Seq[TokenId] = pastEvents
      .flatMap(_.flattenedOrder)
      .collect{
        case Event.Created(id) => id
      }
    
    def archivedTokenIds: Set[TokenId] = pastEvents.collect {
      case Event.Used(id, consumed, consequences) =>
        Option.when(consumed)(id).toSeq ++ PastEvents.unsafe(consequences:_*).archivedTokenIds
    }.flatten.toSet
    
    def activeTokenIds: Seq[TokenId] = {
      val archivedTokens = archivedTokenIds
      tokenIdsSortedByCreation.filterNot(archivedTokens)
    }
  }
