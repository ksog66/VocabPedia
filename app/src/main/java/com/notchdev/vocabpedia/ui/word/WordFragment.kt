package com.notchdev.vocabpedia.ui.word

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.notchdev.vocabpedia.R
import com.notchdev.vocabpedia.VocabViewModel
import com.notchdev.vocabpedia.VocabViewModelFactory
import com.notchdev.vocabpedia.databinding.FragmentWordBinding
import com.notchdev.vocabpedia.data.source.repository.VocabRepository
import com.notchdev.vocabpedia.data.source.local.WordDatabase


class WordFragment : Fragment(),SearchView.OnQueryTextListener {


    private var _binding: FragmentWordBinding? = null

    private lateinit var viewModel: VocabViewModel
    private var word: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vocabRepository = VocabRepository(WordDatabase(requireContext()))
        val viewModelProviderFactory =
            VocabViewModelFactory(activity?.application!!, vocabRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(VocabViewModel::class.java)
        arguments?.apply {
            word = getString(getString(R.string.search_term))
        }
        getWord(word!!)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWordBinding.inflate(layoutInflater)

        viewModel.isLoading.observe({lifecycle}) {
            it?.let {
                if(it) {
                    _binding?.apply {
                        wordLl.visibility = View.INVISIBLE
                        loadingPb.visibility = View.VISIBLE
                    }
                } else {
                    _binding?.apply {
                        wordLl.visibility = View.VISIBLE
                        loadingPb.visibility = View.INVISIBLE
                    }
                }
            }
        }

        return _binding?.root
    }

    private fun getWord(wordQuery:String) {
        viewModel.findWord(wordQuery)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply{
            wordTv.text = word
            termSv.setOnQueryTextListener(this@WordFragment)
        }
        viewModel.wordData.observe({ lifecycle }) {
            if (it != null) {
                _binding?.apply {
                    wordLl.visibility = View.VISIBLE
                    defTv.text = it.shortdef[0]
                }
            }
        }

        viewModel.dataFetchState.observe({lifecycle}) {
            it?.let {
                if(!it) {
                    _binding?.apply {
                        wordLl.visibility = View.INVISIBLE
                        loadingPb.visibility = View.INVISIBLE
                        wordErrorTv.apply {
                            visibility = View.VISIBLE
                            text = getString(R.string.fetch_error,word!!)
                            termSv.visibility = View.VISIBLE
                            termSv.requestFocus()
                            wordTv.visibility = View.GONE
                            termSv.setQuery(word!!,false)
                        }
                    }
                } else {
                    _binding?.apply {
                        wordTv.visibility = View.VISIBLE
                        wordTv.text = word!!
                        termSv.visibility = View.GONE
                        wordLl.visibility = View.VISIBLE
                        wordErrorTv.visibility = View.INVISIBLE
                    }
                }
            }
        }

        _binding?.apply {
            addWordBtn.setOnClickListener {
                val shortDef = defTv.text.toString()
                viewModel.addWord(word!!, shortDef)
                Toast.makeText(context, "$word Added To Your Feed", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query!= null) {
            word = query
            getWord(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}