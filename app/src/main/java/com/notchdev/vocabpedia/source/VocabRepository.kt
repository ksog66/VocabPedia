package com.notchdev.vocabpedia.source

import com.notchdev.vocabpedia.source.api.VocabClient
import com.notchdev.vocabpedia.source.api.modal.Thesarus

object VocabRepository {

    suspend fun getKeyword(word:String): Thesarus? {
        val response = VocabClient.vocabAPI.getMeaning(word = word)
        return response.body()
    }
}