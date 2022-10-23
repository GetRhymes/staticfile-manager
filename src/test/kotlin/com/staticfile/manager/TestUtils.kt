package com.staticfile.manager

import com.staticfile.manager.util.isFileName
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TestUtils {

    @Test
    fun testIfFileName() {
        //positive
        assertTrue("/file.pdf".isFileName())
        assertTrue("/idea/file.html".isFileName())
        assertTrue("/idea/welco3e.html".isFileName())
        assertTrue("/0.1.1/wel23_come.html".isFileName())
        assertTrue("/idea.20/wel23_come.html".isFileName())
        assertTrue("/idea/2020.20/wel23_come.html".isFileName())
        assertTrue("wel23-come.html".isFileName())

        //negative
        assertFalse("/file.d".isFileName())
        assertFalse("/file20.20".isFileName())
        assertFalse("/idea/0.1.20".isFileName())
        assertFalse("/idea/0.1.203".isFileName())
        assertFalse("/idea/0.1.pdf".isFileName())
        assertFalse("/idea/0.1.html".isFileName())
        assertFalse("/idea/0.1.json".isFileName())
        assertFalse("/idea/0.1.json".isFileName())
        assertFalse("/idea/.json".isFileName())
        assertFalse("/idea/.html".isFileName())
    }
}