package com.notchdev.vocabpedia.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.notchdev.vocabpedia.R
import com.notchdev.vocabpedia.databinding.FragmentFeedBinding


class FeedFragment : Fragment() {

    private var _binding:FragmentFeedBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(layoutInflater)

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}