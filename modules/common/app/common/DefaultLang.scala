package common

import play.api.i18n.Lang

trait DefaultLang {

    implicit lazy val defaultLang: Lang = Lang.defaultLang

}
