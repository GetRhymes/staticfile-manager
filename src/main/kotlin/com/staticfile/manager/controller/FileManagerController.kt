package com.staticfile.manager.controller

import com.staticfile.manager.service.FileManagerService
import com.staticfile.manager.util.LOCATION
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class FileManagerController(private val fileManagerService: FileManagerService) {

    private val logger = LoggerFactory.getLogger(FileManagerController::class.java)

    @GetMapping("**/{.:.+?\\..+}")
    fun getPage(request: HttpServletRequest): ResponseEntity<String> {
        logger.info("Request for get page; endpoint: {}", request.servletPath)
        return ResponseEntity(fileManagerService.getDocumentByPath(request.servletPath), HttpStatus.OK)
    }

    @GetMapping("**/{.:\\w+}")
    fun redirect(request: HttpServletRequest): ResponseEntity<String> {
        val newPath = fileManagerService.getRedirectPath(request.servletPath)
        logger.info(
                "Request for redirect; original endpoint: {}; redirect endpoint: {}",
                request.servletPath,
                newPath
        )
        return ResponseEntity(redirectHeader(request.contextPath + newPath), HttpStatus.FOUND)
    }

    private fun redirectHeader(path: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.add(LOCATION, path)
        return headers
    }
}