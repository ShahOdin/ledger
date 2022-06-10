import scala.collection.MapFactory
import scala.collection.immutable.{HashMap, ListMap, TreeMap}
import Api.*

object Domain:
  opaque type ChronologicalIndex = Int
  object ChronologicalIndex:
    //todo: 0 or 1?
    val min: ChronologicalIndex = 1
    extension (value: ChronologicalIndex) {
      def next :ChronologicalIndex = value + 1
    }

  opaque type ActiveTokenLog = Map[TokenId, ChronologicalIndex]
  object ActiveTokenLog:
    def emptyHashMap: ActiveTokenLog = HashMap.empty
    def emptyTreeMap: ActiveTokenLog = TreeMap.empty
    def emptyListMap: ActiveTokenLog = ListMap.empty

    extension (value: ActiveTokenLog){
      //O(1) ~ O(n) depending on underlying map impl
      def withNewToken(tokenId: TokenId, creationOrder: ChronologicalIndex): ActiveTokenLog = value
        .updated(tokenId, creationOrder)

      //O(1) ~ O(n) depending on underlying map impl
      def markAsConsumed(tokenId: TokenId): ActiveTokenLog = value
        .removed(tokenId)

      //O(n log n) time complexity in terms of the size of active tokens
      def extractActiveTokens: ActiveTokens = ActiveTokens.of(
          value
            .toSeq
            .sortBy(_._2)
            .map(_._1)
        )
    }
