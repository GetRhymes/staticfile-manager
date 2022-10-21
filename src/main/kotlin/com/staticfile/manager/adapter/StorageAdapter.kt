package com.staticfile.manager.adapter

interface StorageAdapter {
    fun getDocumentByPath(path: String): String
    fun referenceToAbsolutePath(referencePath: String): String
}