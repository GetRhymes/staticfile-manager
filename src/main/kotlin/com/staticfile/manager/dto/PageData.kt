package com.staticfile.manager.dto

data class PageData(val path: String, val type: Type, val content: String? = null)

enum class Type {
    REDIRECT, OK, NOT_FOUND
}