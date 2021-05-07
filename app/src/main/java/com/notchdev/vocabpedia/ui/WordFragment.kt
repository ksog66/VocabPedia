package com.notchdev.vocabpedia.ui

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.notchdev.vocabpedia.R
import com.notchdev.vocabpedia.VocabViewModel
import com.notchdev.vocabpedia.VocabViewModelFactory
import com.notchdev.vocabpedia.databinding.FragmentWordBinding
import com.notchdev.vocabpedia.source.VocabRepository
import com.notchdev.vocabpedia.source.local.WordDatabase


class WordFragment : Fragment() {


    private var _binding: FragmentWordBinding? = null

    private lateinit var viewModel: VocabViewModel
    private var word: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWordBinding.inflate(layoutInflater)
        val vocabRepository = VocabRepository(WordDatabase(requireContext()))
        val viewModelProviderFactory =
            VocabViewModelFactory(activity?.application!!, vocabRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(VocabViewModel::class.java)
        arguments?.apply {
            word = getString(getString(R.string.search_term))
        }
        getWord()
        return _binding?.root
    }

    private fun getWord() {
        viewModel.searchWord(word!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.wordTv?.text = word
        viewModel.wordData.observe({ lifecycle }) {
            if (it != null) {
                _binding?.apply {
                    wordLl.visibility = View.VISIBLE
                    defTv.text = it.shortdef[0]
                }
            } else {
                _binding?.apply {
                    wordLl.visibility = View.INVISIBLE
                    wordErrorTv.visibility = View.VISIBLE
                }
            }
        }

        _binding?.apply {
            addWordBtn.setOnClickListener {
                val shortDef = defTv.text.toString()
                Log.d("WordFragment",shortDef)
                viewModel.addWord(word!!, shortDef)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}