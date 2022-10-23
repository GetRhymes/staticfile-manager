package com.staticfile.manager.service

import com.staticfile.manager.adapter.StorageAdapter
import com.staticfile.manager.dto.Type
import com.staticfile.manager.util.LOCATION
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class FileManagerService(@Qualifier("amazonS3Adapter") private val adapter: StorageAdapter) {
    private val logger = LoggerFactory.getLogger(FileManagerService::class.java)
    fun getPageOrRedirect(request: HttpServletRequest): ResponseEntity<String> {
        val pageData = adapter.getDocumentOrAbsolutePath(request.servletPath)
        return when(pageData.type) {
            Type.OK -> {
                logger.info("Return page with endpoint: {}", pageData.path)
                ResponseEntity(pageData.content, HttpStatus.OK)
            }
            Type.REDIRECT -> {
                logger.info("Redirect to page: {}", pageData.path)
                ResponseEntity(redirectHeader(request.contextPath + pageData.path), HttpStatus.FOUND)
            }
            else -> {
                logger.info("Page not found: {}", request.servletPath)
                ResponseEntity(pageData.content, HttpStatus.NOT_FOUND)
            }
        }
    }
    private fun redirectHeader(path: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.add(LOCATION, path)
        return headers
    }

}
