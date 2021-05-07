package com.notchdev.vocabpedia.source

import androidx.lifecycle.LiveData
import com.notchdev.vocabpedia.source.api.VocabClient
import com.notchdev.vocabpedia.source.api.modal.Thesarus
import com.notchdev.vocabpedia.source.api.modal.ThesarusItem
import com.notchdev.vocabpedia.source.local.Word
import com.notchdev.vocabpedia.source.local.WordDao
import com.notchdev.vocabpedia.source.local.WordDatabase

class VocabRepository(
    private val db: WordDatabase
) {

    val allWords: LiveData<List<Word>> = db.getWordDao().getAllWord()

    suspend fun getKeyword(word: String): ThesarusItem? {
        val response = VocabClient.vocabAPI.getMeaning(word = word)
        return response.body()?.get(0)
    }

    suspend fun addWord(term: String, shortDef: String) {
        db.getWordDao().insert(
            Word(
                term = term,
                shorDef = shortDef
            )
        )
    }

}