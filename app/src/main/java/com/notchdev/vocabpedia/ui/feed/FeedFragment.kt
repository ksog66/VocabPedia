package com.notchdev.vocabpedia.ui.feed

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.notchdev.vocabpedia.R
import com.notchdev.vocabpedia.VocabViewModel
import com.notchdev.vocabpedia.VocabViewModelFactory
import com.notchdev.vocabpedia.WordAdapter
import com.notchdev.vocabpedia.databinding.FragmentFeedBinding
import com.notchdev.vocabpedia.data.source.repository.VocabRepository
import com.notchdev.vocabpedia.data.source.local.WordDatabase
import java.util.*


class FeedFragment : Fragment(), SearchView.OnQueryTextListener, TextToSpeech.OnInitListener {

    private var _binding:FragmentFeedBinding? = null
    private lateinit var viewModel:VocabViewModel
    private lateinit var wordAdapter: WordAdapter
    private lateinit var textToSpeech:TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textToSpeech = TextToSpeech(activity,this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(layoutInflater)
        val vocabRepository = VocabRepository(WordDatabase(requireContext()))
        val viewModelProviderFactory = VocabViewModelFactory(activity?.application!!,vocabRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(VocabViewModel::class.java)

        wordAdapter = WordAdapter { onAudioClicked(it)}

        _binding?.roomRv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = wordAdapter
        }
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            wordSv.setOnQueryTextListener(this@FeedFragment)
            quizBtn.setOnClickListener {
                findNavController().navigate(
                    R.id.action_navigate_to_navigation_quiz
                )
            }
        }
        viewModel.allWord.observe({lifecycle}) {
            it?.let {
                wordAdapter.update(it)
                Log.d("FeedFragment",it.toString())
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query!=null) {
            searchWord(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun searchWord(word:String) {
        findNavController().navigate(
            R.id.action_navigate_to_navigation_word,
            bundleOf(
                getString(R.string.search_term) to word
            )
        )
    }

    private fun onAudioClicked(word:String) {
        textToSpeech.speak(word,TextToSpeech.QUEUE_FLUSH,null)
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS) {

            val result = textToSpeech.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            }
        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(textToSpeech!=null) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
}