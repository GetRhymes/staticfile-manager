package com.staticfile.manager.adapter.impl

import com.staticfile.manager.adapter.StorageAdapter
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

    override fun getDocumentByPath(path: String): String {
        return try {
            val objectArgs = GetObjectArgs.builder().bucket(bucket).`object`(path).build()
            val result = minioClient.getObject(objectArgs).readBytes()
            String(result)
        } catch (e: ErrorResponseException) {
            val objectArgs = GetObjectArgs
                .builder().bucket(bucket).`object`(NOT_FOUND_PAGE).build()
            val result = minioClient.getObject(objectArgs).readBytes()
            String(result)
        }
    }

    override fun referenceToAbsolutePath(referencePath: String): String {
        val request = GetObjectArgs
            .builder()
            .bucket(bucket)
            .`object`("$referencePath$META_INF_NAME")
            .build()

        return try {
            val bytesMetaData = minioClient.getObject(request).readBytes()
            val metaData = MetaData.getInstance(bytesMetaData)

            when {
                metaData.welcomePage != null -> "${metaData.currentNode}${metaData.welcomePage}"
                metaData.actualNode != null -> {
                    referenceToAbsolutePath("${metaData.currentNode}${metaData.actualNode}")
                }
                else -> NOT_FOUND_PAGE
            }
        } catch (e: ErrorResponseException) {
            NOT_FOUND_PAGE
        }
    }
}
