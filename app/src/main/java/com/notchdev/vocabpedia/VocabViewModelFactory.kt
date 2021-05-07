package com.notchdev.vocabpedia

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.notchdev.vocabpedia.source.VocabRepository

class VocabViewModelFactory(
    private val app: Application,
    private val vocabRepository: VocabRepository
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VocabViewModel(app,vocabRepository) as T
    }
}