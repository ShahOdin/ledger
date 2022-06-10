import Api.*

object TestData:
  val (a, b, c, d, e) = (
    TokenId.of("a"),
    TokenId.of("b"),
    TokenId.of("c"),
    TokenId.of("d"),
    TokenId.of("e")
  )

  /**
   * Sample set of events corresponding to the chain of events depicted in the readme.
  **/
  val events: PastEvents = PastEvents.unsafe(
    Event.Created(a),
    Event.Created(b),
    Event.Used(
      a,
      false,
      Seq(
        Event.Used(
          a,
          true,
          Seq(
            Event.Created(c),
            Event.Used(b, false, Seq.empty)
          )
        ),
        Event.Used(c, true, Seq.empty)
      )
    ),
    Event.Created(d),
    Event.Used(
      d,
      false,
      Seq(Event.Created(e))
    )
  )
