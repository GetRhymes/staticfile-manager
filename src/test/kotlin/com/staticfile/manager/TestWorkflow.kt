package com.staticfile.manager

import com.amazonaws.services.s3.AmazonS3
import com.staticfile.manager.util.STORAGE_BUCKET
import com.staticfile.manager.util.STORAGE_PORT
import io.findify.s3mock.S3Mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.File
import java.nio.file.Files


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class TestWorkflow {

    private var s3ServerMock: S3Mock? = null

    @Autowired
    private val amazonS3Client: AmazonS3? = null

    @Value("\${$STORAGE_PORT}")
    private val storagePort: Int? = null

    @Value("\${$STORAGE_BUCKET}")
    private val bucket: String? = null

    private val tempDir = Files.createTempDirectory("test-storage")

    @Autowired
    private val mockMvc: MockMvc? = null

    @BeforeAll
    fun setup() {
        s3ServerMock = S3Mock.create(storagePort!!, tempDir.toString())
        s3ServerMock!!.start()

        amazonS3Client!!.createBucket(bucket)
        val dataset = File("minio/dataset")
        val listFiles = mutableListOf<String>()
        collectListFiles(dataset, listFiles)
        listFiles.forEach { fileName ->
            val file = File(fileName)
            amazonS3Client.putObject(bucket, fileName.replace("minio/dataset", ""), file)
        }
    }

    @Test
    fun testRedirectToRootWelcomePage() {
        assertThat(
            mockMvc!!
                .perform(get("/help").contextPath("/help"))
                .andExpect(redirectedUrl("/help/welcome.html"))
                .andExpect(status().isFound)
        )
    }

    @Test
    fun testRedirectToActualVersionWelcomePage() {
        assertThat(
            mockMvc!!
                .perform(get("/help/idea").contextPath("/help").servletPath("/idea"))
                .andExpect(redirectedUrl("/help/idea/0.0.2/getting-started.html"))
                .andExpect(status().isFound)
        )
    }

    @Test
    fun testGetRootWelcomePage() {
        assertThat(
            mockMvc!!
                .perform(
                    get("/help/welcome.html")
                        .contextPath("/help")
                        .servletPath("/welcome.html")
                )
                .andExpect(status().isOk)
        )
    }

    @Test
    fun testGetJSONFile() {
        assertThat(
            mockMvc!!
                .perform(
                    get("/help/tools/welcome.json")
                        .contextPath("/help")
                        .servletPath("/tools/welcome.json")
                )
                .andExpect(status().isOk)
        )
    }

    @Test
    fun errorPage() {
        assertThat(
            mockMvc!!
                .perform(
                    get("/help/main.html")
                        .contextPath("/help")
                        .servletPath("/main.html")
                )
                .andExpect(status().isNotFound)
        )
        assertThat(
            mockMvc
                .perform(
                    get("/help/main")
                        .contextPath("/help")
                        .servletPath("/main")
                )
                .andExpect(status().isNotFound)
        )
    }


    @AfterAll
    fun shutdown() {
        s3ServerMock!!.shutdown()
        File(tempDir.toString()).deleteRecursively()
    }

    private fun collectListFiles(directory: File, listFiles: MutableList<String>): MutableList<String> {
        directory.listFiles()!!.forEach { file ->
            if (file.isDirectory) {
                collectListFiles(file, listFiles)
            } else {
                listFiles.add(file.path)
            }
        }
        return listFiles
    }
}
