package com.notchdev.vocabpedia.source

import com.notchdev.vocabpedia.source.api.VocabClient
import com.notchdev.vocabpedia.source.api.modal.Thesarus
import com.notchdev.vocabpedia.source.api.modal.ThesarusItem

object VocabRepository {

    suspend fun getKeyword(word:String): ThesarusItem? {
        val response = VocabClient.vocabAPI.getMeaning(word = word)
        return response.body()?.get(0)
    }
}