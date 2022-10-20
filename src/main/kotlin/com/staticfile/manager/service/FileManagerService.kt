package com.staticfile.manager.service

import com.staticfile.manager.adapter.StorageAdapter
import org.springframework.stereotype.Service

@Service
class FileManagerService(private val adapter: StorageAdapter) {

    fun getDocumentByPath(path: String): String {
        return adapter.getDocumentByPath(path)
    }

    fun getRedirectPath(originPath: String): String {
        return "/nikita.html"
    }

}
