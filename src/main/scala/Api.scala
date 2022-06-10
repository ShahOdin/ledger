object Api:
  opaque type TokenId = String
  object TokenId:
    def of(id: String): TokenId = id
    //NOT essential to the Ledger impl.
    implicit val order: Ordering[TokenId] = Ordering.String
  
  //todo: consider not exposing the full Seq[Event] api
  opaque type PastEvents <: Seq[Event] = Seq[Event]
  object PastEvents:
    def unsafe(value: Event*): PastEvents = value
    def empty: PastEvents = Seq.empty
    //todo: write a safe constructor to validate the input.

  sealed trait Event:
    val tokenId: TokenId
  object Event:
    final case class Created(tokenId: TokenId) extends Event
    final case class Used(
                           tokenId: TokenId,
                           consumed: Boolean,
                           consequences: Seq[Event]
                         ) extends Event

  opaque type ActiveTokens = Seq[TokenId]
  object ActiveTokens:
    def of(value: Seq[TokenId]): ActiveTokens = value
