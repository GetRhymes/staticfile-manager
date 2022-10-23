package com.staticfile.manager.adapter.impl

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3
import com.staticfile.manager.adapter.StorageAdapter
import com.staticfile.manager.dto.MetaData
import com.staticfile.manager.dto.PageData
import com.staticfile.manager.dto.Type
import com.staticfile.manager.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AmazonS3Adapter(private val amazonS3Client: AmazonS3) : StorageAdapter {

    @Value("\${$STORAGE_BUCKET}")
    private val bucket: String? = null

    override fun getDocumentOrAbsolutePath(path: String): PageData {
        return if (path.isFileName()) {
            getDocumentByPath(path)
        } else {
            getAbsolutePath(path)
        }
    }

    private fun getDocumentByPath(path: String): PageData {
        return try {
            val result = amazonS3Client.getObject(bucket, path).objectContent.readBytes()
            return PageData(path, Type.OK, String(result))
        } catch (e: AmazonServiceException) {
            getNotFoundPage()
        }
    }

    private fun getAbsolutePath(referencePath: String): PageData {
        return try {
            val bytesMetaData = amazonS3Client
                .getObject(bucket,"$referencePath$META_INF_NAME")
                .objectContent
                .readBytes()
            val metaData = MetaData.getInstance(bytesMetaData)
            when {
                metaData.welcomePage != null -> {
                    PageData("${metaData.currentNode}${metaData.welcomePage}", Type.REDIRECT)
                }
                metaData.actualNode != null -> {
                    getAbsolutePath("${metaData.currentNode}${metaData.actualNode}")
                }
                else -> getNotFoundPage()
            }
        } catch (e: AmazonServiceException) {
            getNotFoundPage()
        }
    }

    private fun getNotFoundPage(): PageData {
        val notFoundPageBytes = amazonS3Client.getObject(bucket, NOT_FOUND_PAGE).objectContent.readBytes()
        return PageData(NOT_FOUND_PAGE, Type.NOT_FOUND, String(notFoundPageBytes))
    }
}
