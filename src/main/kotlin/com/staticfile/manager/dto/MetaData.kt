package com.staticfile.manager.dto

import com.staticfile.manager.util.ACTUAL_NODE
import com.staticfile.manager.util.CURRENT_NODE
import com.staticfile.manager.util.WELCOME_PAGE

data class MetaData(
    val currentNode: String,
    val welcomePage: String?,
    val actualNode: String?
) {
    companion object {
        fun getInstance(bytes: ByteArray): MetaData {
            val linesMetaInf = String(bytes).replace(Regex("""['"]"""), "").split("\n")

            var currentNode = linesMetaInf
                .find { it.contains(CURRENT_NODE) }!!
                .split(":")[1]
                .trim()

            if (currentNode.length == 1) currentNode = "" // if (currentNode == "/")

            val welcomePage = linesMetaInf
                .find { it.contains(WELCOME_PAGE) }
                ?.split(":")
                ?.get(1)
                ?.trim()

            val actualNode = linesMetaInf
                .find { it.contains(ACTUAL_NODE) }
                ?.split(":")
                ?.get(1)
                ?.trim()

            return MetaData(currentNode, welcomePage, actualNode)
        }
    }
}
