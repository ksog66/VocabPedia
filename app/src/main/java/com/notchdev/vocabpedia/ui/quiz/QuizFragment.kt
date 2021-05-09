package com.notchdev.vocabpedia.ui.quiz

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.notchdev.vocabpedia.R
import com.notchdev.vocabpedia.data.model.Quiz
import com.notchdev.vocabpedia.data.model.Word
import com.notchdev.vocabpedia.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private lateinit var viewModel: QuizViewModel
    private var quizList = ArrayList<Quiz>()
    private var currentPosition:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)
        ).get(QuizViewModel::class.java)

        viewModel.allWord.observe({lifecycle}) {
            it?.let {
                convertWordToQuiz(it)
                Log.d("quiz",it.toString())
            }
        }
    }

    private fun convertWordToQuiz(wordList: List<Word>) {

        wordList.forEach { word ->
            val options: MutableList<String> =
                wordList.map { it.shorDef }.filter { it != word.shorDef }.shuffled()
                    .take(3) as MutableList<String>

            options.apply {
                add(word.shorDef)
                shuffle()
            }
            val ansPos = options.indexOf(word.shorDef)
            val quiz =
                Quiz(question = word.term, answer = ansPos, option = options as ArrayList<String>)
            quizList.add(quiz)
        }
        initQuiz()
    }

    private fun initQuiz() {
        val quiz = quizList[0]
        updateUI(quiz)
    }

    private fun updateUI(quiz: Quiz) {
        _binding?.apply {
            questionNoTv.setText("${quizList.indexOf(quiz) + 1}/${quizList.size}")
            questionTv.text = quiz.question
            val option = quiz.option
            rb1.text = option[0]
            rb2.text = option[1]
            rb3.text = option[2]
            rb4.text = option[3]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizBinding.inflate(layoutInflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.apply {
            nextBtn.setOnClickListener {
                ++currentPosition
                updateUI(quizList[currentPosition])
            }
            prevBtn.setOnClickListener {
                if(currentPosition != 0) {
                    --currentPosition
                    updateUI(quizList[currentPosition])
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}