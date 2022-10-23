package com.staticfile.manager.util

fun String.isFileName(): Boolean {
    return this
        .split("/")
        .last()
        .matches(Regex("""[\w\-]+\.[a-zA-Z]{2,5}"""))
}