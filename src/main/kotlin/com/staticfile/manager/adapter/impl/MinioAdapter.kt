package com.staticfile.manager.adapter.impl

import com.staticfile.manager.adapter.StorageAdapter
import com.staticfile.manager.dto.MetaData
import com.staticfile.manager.dto.PageData
import com.staticfile.manager.dto.Type
import com.staticfile.manager.util.*
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.errors.ErrorResponseException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MinioAdapter(private val minioClient: MinioClient) : StorageAdapter {

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
            val objectArgs = GetObjectArgs.builder().bucket(bucket).`object`(path).build()
            val result = minioClient.getObject(objectArgs).readBytes()
            return PageData(path, Type.OK, String(result))
        } catch (e: ErrorResponseException) {
            getNotFoundPage()
        }
    }

    private fun getAbsolutePath(path: String): PageData {
        val request = GetObjectArgs
            .builder()
            .bucket(bucket)
            .`object`("$path$META_INF_NAME")
            .build()

        return try {
            val bytesMetaData = minioClient.getObject(request).readBytes()
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
        } catch (e: ErrorResponseException) {
            getNotFoundPage()
        }
    }

    private fun getNotFoundPage(): PageData {
        val objectArgs = GetObjectArgs.builder().bucket(bucket).`object`(NOT_FOUND_PAGE).build()
        val result = minioClient.getObject(objectArgs).readBytes()
        return PageData(NOT_FOUND_PAGE, Type.NOT_FOUND, String(result))
    }
}
