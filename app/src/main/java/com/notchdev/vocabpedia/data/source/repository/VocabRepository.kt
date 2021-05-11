package com.notchdev.vocabpedia.data.source.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.notchdev.vocabpedia.data.source.api.VocabClient
import com.notchdev.vocabpedia.data.model.ThesarusItem
import com.notchdev.vocabpedia.data.model.Word
import com.notchdev.vocabpedia.data.source.local.WordDatabase
import com.notchdev.vocabpedia.util.Result
class VocabRepository(
    private val db: WordDatabase
) {

    val allWords: LiveData<List<Word>> = db.getWordDao().getAllWord()

    suspend fun getKeyword(word: String): Result<ThesarusItem?> {
        return try {
            val response = VocabClient.vocabAPI.getMeaning(word)
            if(response.isSuccessful) {
                val result = response.body()?.get(0)
                Log.i("repo","${result.toString()} successful")
                Result.Success(result)
            } else {
                Log.i("repo","@null")
                Result.Success(null)
            }
        } catch (exception: Exception) {
            Log.i("repo","${exception.localizedMessage} error")
            Result.Error(exception)
        }
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