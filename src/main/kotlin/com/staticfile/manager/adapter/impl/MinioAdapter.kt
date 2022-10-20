package com.staticfile.manager.adapter.impl

import com.staticfile.manager.adapter.StorageAdapter
import com.staticfile.manager.util.STORAGE_BUCKET
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
            getNotFoundPage()
        }
    }

    override fun getNotFoundPage(): String {
        return """
            <!DOCTYPE html>
            <html>
            <body>

            <h1>404 Not found!</h1>

            <p>Tut ni4ego net :)</p>

            </body>
            </html>
        """.trimIndent()
    }
}
