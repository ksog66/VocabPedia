package com.notchdev.vocabpedia.data.source

import com.notchdev.vocabpedia.data.source.api.VocabClient
import com.notchdev.vocabpedia.data.modal.Thesarus

object VocabRepository {

    suspend fun getKeyword(word:String): Thesarus? {
        val response = VocabClient.vocabAPI.getMeaning(word = word)
        return response.body()
    }
}