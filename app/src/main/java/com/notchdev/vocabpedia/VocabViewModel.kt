package com.notchdev.vocabpedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notchdev.vocabpedia.source.VocabRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VocabViewModel:ViewModel() {


    fun searchWord(searchTerm:String) = viewModelScope.launch(Dispatchers.IO){
         VocabRepository.getKeyword(searchTerm)
    }
}