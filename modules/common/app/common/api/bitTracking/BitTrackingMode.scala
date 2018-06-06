package common.api.bitTracking

import scala.collection.SortedMap

object BitTrackingMode extends Enumeration {

    type BitTrackingMode = Value

    val COLLECTIVE: BitTrackingMode = Value("collective")
    val SINGLE_CHEER: BitTrackingMode = Value("singleCheer")

    val map: SortedMap[String, String] = SortedMap(values.map(p =>
        p.id.toString -> s"dashboard.bitTracking.cheerModes.$p"
    ).toSeq: _*)

}
