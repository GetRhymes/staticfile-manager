package com.staticfile.manager.configuration

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.staticfile.manager.util.*
import io.minio.MinioClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Lazy


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
        logger.info("Minio storage client was created. Connection to {}", endpoint)
        return minioClient
    }

    @Bean
    fun amazonS3Client(): AmazonS3 {
        val credentials = BasicAWSCredentials(accessKey, privateKey)
        val credentialsProvider = AWSStaticCredentialsProvider(credentials)

        val endpoint = "$storageProtocol://$storageHost:$storagePort"
        val endpointConfiguration = EndpointConfiguration(endpoint, Regions.US_EAST_1.name)

        logger.info("AmazonS3 client was created. Connection to {}", endpoint)
        return AmazonS3ClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withEndpointConfiguration(endpointConfiguration)
            .withPathStyleAccessEnabled(true)
            .build()
    }
}
