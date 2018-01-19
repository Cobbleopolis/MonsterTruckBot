package common.api.bitTracking

import scala.collection.SortedMap

object GameCheerMode extends Enumeration {

    type GameCheerMode = Value

    val COLLECTIVE: GameCheerMode = Value("collective")
    val SINGLE_CHEER: GameCheerMode = Value("singleCheer")

    val map: SortedMap[String, String] = SortedMap(values.map(p =>
        p.toString -> s"dashboard.bitTracking.cheerModes.$p"
    ).toSeq: _*)

}
