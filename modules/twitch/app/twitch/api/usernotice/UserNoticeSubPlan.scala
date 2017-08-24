package twitch.api.usernotice

object UserNoticeSubPlan extends Enumeration {

    type UserNoticeSubPlan = Value

    val UNKNOWN: UserNoticeSubPlan = Value("Unknown")
    val PRIME: UserNoticeSubPlan = Value("Prime")
    val TIER_1000: UserNoticeSubPlan = Value("1000")
    val TIER_2000: UserNoticeSubPlan = Value("3000")
    val TIER_3000: UserNoticeSubPlan = Value("3000")

}
