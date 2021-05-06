package com.notchdev.vocabpedia.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.notchdev.vocabpedia.R
import com.notchdev.vocabpedia.VocabViewModel
import com.notchdev.vocabpedia.databinding.FragmentFeedBinding


class FeedFragment : Fragment(),androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private var _binding:FragmentFeedBinding? = null
    private val viewModel:VocabViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(layoutInflater)

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            wordSv.setOnQueryTextListener(this@FeedFragment)
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