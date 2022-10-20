package com.staticfile.manager.configuration

import com.staticfile.manager.util.*
import io.minio.MinioClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StorageConfiguration {

    private val logger: Logger = LoggerFactory.getLogger(StorageConfiguration::class.java)

    @Value("\${$STORAGE_PROTOCOL}")
    private val storageProtocol: String? = null

    @Value("\${$STORAGE_HOST}")
    private val storageHost: String? = null

    @Value("\${$STORAGE_PORT}")
    private val storagePort: Int? = null

    @Value("\${$STORAGE_ACCESS_KEY}")
    private val accessKey: String? = null

    @Value("\${$STORAGE_PRIVATE_KEY}")
    private val privateKey: String? = null

    @Bean
    fun minioClient(): MinioClient {
        val endpoint = "$storageProtocol://$storageHost:$storagePort"
        val minioClient = MinioClient
                .builder()
                .credentials(accessKey, privateKey)
                .endpoint(endpoint)
                .build()
        logger.info("Storage client was created. Connection to {}", endpoint)
        return minioClient
    }
}
