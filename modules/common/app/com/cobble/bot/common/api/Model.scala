package com.cobble.bot.common.api

import anorm.NamedParameter


abstract class Model {

    val namedParameters: Seq[NamedParameter]

}
