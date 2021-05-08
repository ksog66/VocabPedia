package com.notchdev.vocabpedia

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.notchdev.vocabpedia.source.VocabRepository
import com.notchdev.vocabpedia.source.api.modal.ThesarusItem
import com.notchdev.vocabpedia.source.local.Word
import com.notchdev.vocabpedia.source.local.WordDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.notchdev.vocabpedia.util.Result

class VocabViewModel(
    app: Application,
    val repository: VocabRepository
) : AndroidViewModel(app) {

    private val _wordData = MutableLiveData<ThesarusItem>()
    val wordData: LiveData<ThesarusItem> = _wordData

    private val _dataFetchState = MutableLiveData<Boolean>()
    val dataFetchState: LiveData<Boolean> = _dataFetchState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading:LiveData<Boolean> = _isLoading

    val allWord: LiveData<List<Word>>

    init {
        allWord = repository.allWords
    }


    fun searchWord(searchTerm: String) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = repository.getKeyword(searchTerm)) {
                is Result.Success -> {
                    _isLoading.postValue(false)
                    if(result.data!=null) {
                        Log.i("viewModel",result.toString())
                        val wordResult = result.data
                        _dataFetchState.postValue(true)
                        _wordData.postValue(wordResult)
                    }
                }
                is Result.Error -> {
                    _isLoading.postValue(false)
                    _dataFetchState.postValue(false)
                }
                is Result.Loading -> {
                    Log.i("viewModel", "loading")
                    _isLoading.postValue(true)
                }
            }
        }
    }

    fun addWord(term: String, shortDef: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.addWord(term, shortDef)
    }

}