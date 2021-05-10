package com.notchdev.vocabpedia.ui.feed

import android.os.Bundle
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


class FeedFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding:FragmentFeedBinding? = null
    private lateinit var viewModel:VocabViewModel
    private lateinit var wordAdapter: WordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(layoutInflater)
        val vocabRepository = VocabRepository(WordDatabase(requireContext()))
        val viewModelProviderFactory = VocabViewModelFactory(activity?.application!!,vocabRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(VocabViewModel::class.java)

        wordAdapter = WordAdapter()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}