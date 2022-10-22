package com.staticfile.manager.adapter

import com.staticfile.manager.dto.PageData

interface StorageAdapter {
    fun getDocumentOrAbsolutePath(path: String): PageData
}
