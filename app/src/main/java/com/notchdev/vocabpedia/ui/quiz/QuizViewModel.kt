package com.notchdev.vocabpedia.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.notchdev.vocabpedia.data.model.Quiz
import com.notchdev.vocabpedia.data.source.repository.LocalWordRepository
import com.notchdev.vocabpedia.data.model.Word
import com.notchdev.vocabpedia.data.source.local.WordDatabase

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val localRepository: LocalWordRepository
    val allWord: LiveData<List<Word>>

    init {
        val dao = WordDatabase(application).getWordDao()
        localRepository = LocalWordRepository(dao)
        allWord = localRepository.allWord
    }

}