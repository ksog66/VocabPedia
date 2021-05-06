package com.notchdev.vocabpedia

import com.notchdev.vocabpedia.data.source.api.VocabClient
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun getWord() {
        runBlocking {
            val thesarus = VocabClient.vocabAPI.getMeaning(word = "surprised")
            assertNotNull(thesarus.body()?.get(0)?.meta)
        }
    }
}