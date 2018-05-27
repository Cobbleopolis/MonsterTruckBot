package common.util

import java.text.MessageFormat

import scala.collection.mutable

object MessageFormatUtil {

    def formatVariableMessage(template: String, variables: mutable.LinkedHashMap[String, Object]): String = {
        var formattedMessage: String = template
        variables.keys.zipWithIndex.foreach(kv => formattedMessage = formattedMessage.replaceAll(s"\\{${kv._1}", s"\\{${kv._2}"))
        MessageFormat.format(formattedMessage, variables.values.toSeq: _*)
    }

}
