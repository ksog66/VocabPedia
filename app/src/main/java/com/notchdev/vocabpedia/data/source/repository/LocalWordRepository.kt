package com.notchdev.vocabpedia.data.source.repository

import androidx.lifecycle.LiveData
import com.notchdev.vocabpedia.data.model.Word
import com.notchdev.vocabpedia.data.source.local.WordDao

class LocalWordRepository(wordDao: WordDao) {

    val allWord : LiveData<List<Word>> = wordDao.getAllWord()
}