package twitch.api.usernotice

object UserNoticeMessageId extends Enumeration {

    type UserNoticeMessageId = Value

    val UNKNOWN: UserNoticeMessageId = Value("Unknown")
    val RESUBSCRIPTION: UserNoticeMessageId = Value("resub")
    val SUBSCRIPTION: UserNoticeMessageId = Value("sub")
    val GIFTED_SUBSCRIPTION: UserNoticeMessageId = Value("subgift")
    val MYSTERY_GIFTED_SUBSCRIPTION: UserNoticeMessageId = Value("submysterygift")
    val GIFT_PAID_UPGRADE: UserNoticeMessageId = Value("giftpaidupgrade")
    val CHARITY: UserNoticeMessageId = Value("charity")

}