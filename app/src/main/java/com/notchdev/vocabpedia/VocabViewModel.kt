package com.notchdev.vocabpedia

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.notchdev.vocabpedia.data.source.repository.VocabRepository
import com.notchdev.vocabpedia.data.model.ThesarusItem
import com.notchdev.vocabpedia.data.model.Word
import com.notchdev.vocabpedia.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VocabViewModel(
    app: Application,
    private val repository: VocabRepository
) : AndroidViewModel(app) {

    private val _wordData = MutableLiveData<ThesarusItem>()
    val wordData: LiveData<ThesarusItem> = _wordData

    private val _dataFetchState = MutableLiveData<Boolean>()
    val dataFetchState: LiveData<Boolean> = _dataFetchState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading:LiveData<Boolean> = _isLoading

    val allWord: LiveData<List<Word>> = repository.allWords


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