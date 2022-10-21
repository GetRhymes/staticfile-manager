package com.staticfile.manager.adapter.impl

import com.staticfile.manager.adapter.StorageAdapter
import com.staticfile.manager.util.STORAGE_BUCKET
import io.minio.GetObjectArgs
import io.minio.ListObjectsArgs
import io.minio.MinioClient
import io.minio.StatObjectArgs
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

    private fun referenceToAbsolutePath(referencePath: String): String {
        val requestForListObjects = ListObjectsArgs
                .builder()
                .bucket(bucket)
                .startAfter(referencePath)
                .recursive(false)
                .build()
        val listObjects = minioClient.listObjects(requestForListObjects)
        val onlyFiles = listObjects.filter { !it.get().isDir }

        if (!listObjects.iterator().hasNext()) return "/help/not-found.html"
        if (onlyFiles.isEmpty()) {
            val lastVersion = listObjects.sortedByDescending {
                it.get().objectName()
            }[0].get().objectName()
            val actualVersionDocs = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).startAfter(lastVersion).recursive(false).build())
            var welcomePage = actualVersionDocs.find {
                !it.get().isDir && it.get().objectName().lowercase().contains("welcome")
            }
            return if (welcomePage == null) {
                welcomePage = actualVersionDocs.find { !it.get().isDir }
                if (welcomePage == null) "/help/not-found.html" else welcomePage.get().objectName()
            } else {
                welcomePage.get().objectName()
            }
        } else {
            var welcomePage = onlyFiles.find {
                !it.get().isDir && it.get().objectName().lowercase().contains("welcome")
            }
            return if (welcomePage == null) {
                welcomePage = onlyFiles.find { !it.get().isDir }
                if (welcomePage == null) "/help/not-found.html" else welcomePage.get().objectName()
            } else {
                welcomePage.get().objectName()
            }
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
