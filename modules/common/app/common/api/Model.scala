package common.api

import anorm.NamedParameter


abstract class Model {

    val namedParameters: Seq[NamedParameter]

}
