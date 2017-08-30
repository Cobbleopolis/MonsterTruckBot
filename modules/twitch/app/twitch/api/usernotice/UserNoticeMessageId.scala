package twitch.api.usernotice

object UserNoticeMessageId extends Enumeration {

    type UserNoticeMessageId = Value

    val UNKNOWN: UserNoticeMessageId = Value("Unknown")
    val RESUBSCRIPTION: UserNoticeMessageId = Value("resub")
    val SUBSCRIPTION: UserNoticeMessageId = Value("sub")
    val CHARITY: UserNoticeMessageId = Value("charity")

}