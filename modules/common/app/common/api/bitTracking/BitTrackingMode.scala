package common.api.bitTracking

import scala.collection.SortedMap

object BitTrackingMode extends Enumeration {

    type BitTrackingMode = Value

    val NONE: BitTrackingMode = Value(0)
    val NIP_DIP: BitTrackingMode = Value(1)
    val RBG: BitTrackingMode = Value(2)
    val JACKSHOTS: BitTrackingMode = Value(3)
    val PUSH_UP: BitTrackingMode = Value(4)
    val SING_IT_OR_SLAM_IT: BitTrackingMode = Value(5)

    val map: SortedMap[String, String] = SortedMap(values.map(p =>
        p.id.toString -> s"dashboard.bitTracking.modes.${p.toString.toLowerCase()}"
    ).toSeq: _*)

}
