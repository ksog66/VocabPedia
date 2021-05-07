package com.notchdev.vocabpedia

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notchdev.vocabpedia.source.VocabRepository
import com.notchdev.vocabpedia.source.api.modal.ThesarusItem
import com.notchdev.vocabpedia.source.local.WordDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VocabViewModel(
    application: Application
):ViewModel() {

    val repository: VocabRepository

    init {
        val dao = WordDatabase.getDatabase(application).getWordDao()
        repository = VocabRepository(dao)
    }
    private val _wordData = MutableLiveData<ThesarusItem>()
    val wordData: LiveData<ThesarusItem> = _wordData


    fun searchWord(searchTerm:String) = viewModelScope.launch(Dispatchers.IO){
         repository.getKeyword(searchTerm).let {
             _wordData.postValue(it)
         }
    }

    fun addWord(term:String,shortDef:String) =viewModelScope.launch(Dispatchers.IO){
        repository.addWord(term,shortDef)
    }
}