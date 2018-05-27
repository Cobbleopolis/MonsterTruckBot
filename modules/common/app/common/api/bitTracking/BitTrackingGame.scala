package common.api.bitTracking

import scala.collection.SortedMap

object BitTrackingGame extends Enumeration {

    type BitTrackingGame = Value

    val NONE: BitTrackingGame = Value(0)
    val NIP_DIP: BitTrackingGame = Value(1)
    val RBG: BitTrackingGame = Value(2)
    val JACKSHOTS: BitTrackingGame = Value(3)
    val PUSH_UP: BitTrackingGame = Value(4)
    val SING_IT_OR_SLAM_IT: BitTrackingGame = Value(5)

    val map: SortedMap[String, String] = SortedMap(values.map(p =>
        p.id.toString -> s"dashboard.bitTracking.modes.${p.toString.toLowerCase()}"
    ).toSeq: _*)

}
