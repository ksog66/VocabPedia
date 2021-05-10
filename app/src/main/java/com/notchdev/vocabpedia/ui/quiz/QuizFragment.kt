package com.notchdev.vocabpedia.ui.quiz

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private lateinit var result: Array<Boolean>
    private lateinit var checkedPos: IntArray
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

        activity?.onBackPressedDispatcher?.addCallback(this,object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setMessage("Are you sure you want to go back?")
                    setPositiveButton("Yes") {_,_ ->
                        findNavController().popBackStack()
                    }
                    setNegativeButton("Cancel") { dialog,_ ->
                        dialog.dismiss()
                    }
                    setCancelable(false)
                    create()
                    show()
                }
            }
        })
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

        checkedPos = IntArray(quizList.size) { -1 }
        result = Array(quizList.size) { false }
        quizList.shuffle()
        currentPosition = 0
        val quiz = quizList[0]
        updateUI(quiz)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(quiz: Quiz) {
        _binding?.apply {
            questionNoTv.text = "Question: ${quizList.indexOf(quiz) + 1}/${quizList.size}"
            questionTv.text = quiz.question
            val option = quiz.option
            rb1.text = option[0]
            rb2.text = option[1]
            rb3.text = option[2]
            rb4.text = option[3]
//
                val checkId = when (checkedPos[currentPosition]) {
                    0 -> R.id.rb1
                    1 -> R.id.rb2
                    2 -> R.id.rb3
                    3 -> R.id.rb4
                    else -> -1
                }
                optionRg.check(checkId)
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
            optionRg.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rb1 -> checkAnswer(0)
                    R.id.rb2 -> checkAnswer(1)
                    R.id.rb3 -> checkAnswer(2)
                    R.id.rb4 -> checkAnswer(3)
                    -1 -> checkAnswer(-1)
                }
            }
        }
    }

    private fun checkAnswer(checkedId: Int) {
        checkedPos[currentPosition] = checkedId
        result[currentPosition] = (quizList[currentPosition].answer == checkedId)
        Log.d("quiz2", "$currentPosition")
        Log.d("quiz2", "${checkedPos[currentPosition]}")
    }

    @SuppressLint("SetTextI18n")
    private fun submitAnswer() {
        var score = 0
        result.forEach { answer ->
            if (answer) score++
            Log.d("quiz1", "$score")
        }
        val alertDialog = Dialog(requireContext())
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setCancelable(false)
        val dialogBinding = ScoreDialogLayoutBinding.inflate(LayoutInflater.from(context))
        alertDialog.setContentView(dialogBinding.root)

        dialogBinding.apply {
            scoreTv.text = "$score/${quizList.size}"
            noOfQuestionTv.text = "${quizList.size}"
            retakeBtn.setOnClickListener {
                alertDialog.dismiss()
                initQuiz()
            }
        }
        alertDialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}