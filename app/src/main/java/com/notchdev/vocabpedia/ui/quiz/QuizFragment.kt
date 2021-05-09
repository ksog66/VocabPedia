package com.notchdev.vocabpedia.ui.quiz

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.notchdev.vocabpedia.R
import com.notchdev.vocabpedia.data.model.Quiz
import com.notchdev.vocabpedia.data.model.Word
import com.notchdev.vocabpedia.databinding.FragmentQuizBinding
import com.notchdev.vocabpedia.databinding.ScoreDialogLayoutBinding

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private lateinit var viewModel: QuizViewModel
    private var quizList = ArrayList<Quiz>()
    private var currentPosition: Int = 0
    private var result: MutableList<Pair<Boolean, Int>> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)
        ).get(QuizViewModel::class.java)

        viewModel.allWord.observe({ lifecycle }) {
            it?.let {
                convertWordToQuiz(it)
                Log.d("quiz", it.toString())
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
        quizList.shuffle()
        val quiz = quizList[0]
        updateUI(quiz)
        currentPosition = 0
        result.clear()
    }

    private fun updateUI(quiz: Quiz) {
        _binding?.apply {
            questionNoTv.text = "Question: ${quizList.indexOf(quiz) + 1}/${quizList.size}"
            questionTv.text = quiz.question
            val option = quiz.option
            rb1.text = option[0]
            rb2.text = option[1]
            rb3.text = option[2]
            rb4.text = option[3]
            if (currentPosition < result.size && result[currentPosition] != null) {
                optionRg.check(result[currentPosition].second)
            }
            nextBtn.text = if (currentPosition == quizList.size - 1) "Submit" else "Next"
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
                if (currentPosition == quizList.size - 1) {
                    submitAnswer()
                } else {
                    ++currentPosition
                    updateUI(quizList[currentPosition])
                }
            }
            prevBtn.setOnClickListener {
                if (currentPosition != 0) {
                    --currentPosition
                    updateUI(quizList[currentPosition])
                }
            }
            if (optionRg.checkedRadioButtonId != -1) {
                optionRg.setOnCheckedChangeListener { _, checkedId ->
                    checkAnswer(checkedId)
                }
            }

        }
    }

    private fun checkAnswer(checkedId: Int) {
        val pair = Pair(
            quizList[currentPosition].answer == checkedId
        ,checkedId
        )
        result.add(pair)
    }

    private fun submitAnswer() {
        var score = 0
        result.forEach { answer ->
            if (answer.first) score++
        }
        val alertDialog = Dialog(requireContext())
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setCancelable(false)
        alertDialog.setContentView(R.layout.score_dialog_layout)
        val scoreText = alertDialog.findViewById(R.id.scoreTv) as TextView
        scoreText.text = "$score/${quizList.size}"
        val totalQuestion = alertDialog.findViewById(R.id.questionNoTv) as TextView
        totalQuestion.text = "${quizList.size}"

        val retakeButton = alertDialog.findViewById(R.id.retakeBtn) as Button
        retakeButton.setOnClickListener {
            initQuiz()
        }
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}