package com.notchdev.vocabpedia

import android.app.Application
import androidx.lifecycle.*
import com.notchdev.vocabpedia.source.VocabRepository
import com.notchdev.vocabpedia.source.api.modal.ThesarusItem
import com.notchdev.vocabpedia.source.local.Word
import com.notchdev.vocabpedia.source.local.WordDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VocabViewModel(
    app:Application,
    val repository: VocabRepository
): AndroidViewModel(app) {

    private val _wordData = MutableLiveData<ThesarusItem>()
    val wordData: LiveData<ThesarusItem> = _wordData

//    private val _localWordData = MutableLiveData<List<Word>>()
//    val localWordData: LiveData<List<Word>> = _localWordData
    val allWord : LiveData<List<Word>>
    init {
        allWord = repository.allWords
    }


    fun searchWord(searchTerm:String) = viewModelScope.launch(Dispatchers.IO){
         repository.getKeyword(searchTerm).let {
             _wordData.postValue(it)
         }
    }

    fun addWord(term:String,shortDef:String) =viewModelScope.launch(Dispatchers.IO){
        repository.addWord(term,shortDef)
    }

}