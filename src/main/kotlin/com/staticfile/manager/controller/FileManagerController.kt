package com.staticfile.manager.controller

import com.staticfile.manager.service.FileManagerService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class FileManagerController(private val fileManagerService: FileManagerService) {
    private val logger = LoggerFactory.getLogger(FileManagerController::class.java)

    @GetMapping("**")
    fun getPageOrRedirect(request: HttpServletRequest): ResponseEntity<String> {
        logger.info("Get request to : {}", request.requestURL)
        return fileManagerService.getPageOrRedirect(request)
    }
}
