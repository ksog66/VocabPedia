package com.notchdev.vocabpedia

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import android.widget.Toast
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

    private val _wordData = MutableLiveData<ThesarusItem>(null)
    val wordData: LiveData<ThesarusItem> = _wordData

    private val _dataFetchState = MutableLiveData<Boolean>(false)
    val dataFetchState: LiveData<Boolean> = _dataFetchState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading:LiveData<Boolean> = _isLoading

    val allWord: LiveData<List<Word>> = repository.allWords

    fun findWord(searchTerm:String) {
        if(hasInternetConnection()) {
            searchWord(searchTerm)
        } else {
            _dataFetchState.postValue(false)
            Toast.makeText(getApplication(),"No Internet Connection",Toast.LENGTH_SHORT).show()
        }
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

    private fun hasInternetConnection():Boolean{
        val connectivityManger= getApplication<VocabPediaApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            val activeNetwork=connectivityManger.activeNetwork ?: return false
            val capabilities=connectivityManger.getNetworkCapabilities(activeNetwork) ?: return false

            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) ||
                        capabilities.hasTransport(TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(TRANSPORT_ETHERNET) -> true

                else-> false
            }
        }else{
            connectivityManger.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI,
                    TYPE_MOBILE,
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun addWord(term: String, shortDef: String) = viewModelScope.launch(Dispatchers.IO) {
            repository.addWord(term, shortDef)
    }

    fun deleteWord(id:Long) = viewModelScope.launch {
        repository.deleteWordFromDb(id)
    }

}