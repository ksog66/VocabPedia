package com.notchdev.vocabpedia.ui.feed

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.TimePicker
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.notchdev.vocabpedia.R
import com.notchdev.vocabpedia.VocabViewModel
import com.notchdev.vocabpedia.VocabViewModelFactory
import com.notchdev.vocabpedia.WordAdapter
import com.notchdev.vocabpedia.databinding.FragmentFeedBinding
import com.notchdev.vocabpedia.data.source.repository.VocabRepository
import com.notchdev.vocabpedia.data.source.local.WordDatabase
import com.notchdev.vocabpedia.databinding.ReadMoreLayoutBinding
import com.notchdev.vocabpedia.databinding.ScoreDialogLayoutBinding
import com.notchdev.vocabpedia.util.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit


class FeedFragment : Fragment(), SearchView.OnQueryTextListener, TextToSpeech.OnInitListener {

    private var _binding: FragmentFeedBinding? = null
    private lateinit var viewModel: VocabViewModel
    private lateinit var wordAdapter: WordAdapter
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var sharedPref:SharedPreferences
    private lateinit var myCalendar: Calendar
    private var listSize: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        textToSpeech = TextToSpeech(activity, this)
        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)!!
        setUpNotificationWorker()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.reminder_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_schedule) {
            selectTime()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun selectTime() {
        myCalendar = Calendar.getInstance()
        val timeSetListener =
            TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hour: Int, minute: Int ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hour)
                myCalendar.set(Calendar.MINUTE, minute)
                myCalendar.set(Calendar.SECOND,0)
                updateTime()
            }
        TimePickerDialog(
            requireContext(),
            timeSetListener,
            myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE),
            false
        ).show()

    }

    private fun updateTime() {
        val time = myCalendar.time.time
        val editor = sharedPref.edit()
        editor.apply {
            putLong("TIME",time)
            apply()
        }
        Log.i("feed","$time")
        setUpNotificationWorker()
    }

    private fun setUpNotificationWorker() {
        val timePref = sharedPref.getLong("TIME",0)
        val currentTime = Calendar.getInstance().time.time
        val time = if(currentTime > timePref) currentTime - timePref+ TimeUnit.HOURS.toMillis(24) else timePref - currentTime
        Log.i("feed","final Time=> $time")
        val workerRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(time,TimeUnit.MILLISECONDS)
            .build()



        WorkManager.getInstance(requireContext()).enqueue(workerRequest)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(layoutInflater)
        val vocabRepository = VocabRepository(WordDatabase(requireContext()))
        val viewModelProviderFactory =
            VocabViewModelFactory(activity?.application!!, vocabRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(VocabViewModel::class.java)

        wordAdapter = WordAdapter { onAudioClicked(it) }

        _binding?.feedRv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = wordAdapter
        }
        initSwap()
        return _binding?.root
    }

    private fun initSwap() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        viewModel.deleteWord(wordAdapter.getItemId(position))
                    }
                }
                wordAdapter.notifyDataSetChanged()
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView

                    val paint = Paint()
                    val icon: Bitmap

                    if (dX < 0) {
                        icon = BitmapFactory.decodeResource(resources, R.drawable.icon_delete)

                        paint.color = resources.getColor(R.color.gold)

                        canvas.drawRect(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat(),
                            paint
                        )

                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat() - icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )
                    }
                    viewHolder.itemView.translationX = dX
                } else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(_binding?.feedRv)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            wordSv.setOnQueryTextListener(this@FeedFragment)
            quizBtn.setOnClickListener {
                if (listSize >= 10) {
                    findNavController().navigate(
                        R.id.action_navigate_to_navigation_quiz
                    )
                } else {
                    val alertDialog = Dialog(requireContext())
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    alertDialog.setCancelable(true)
                    val dialogBinding = ReadMoreLayoutBinding.inflate(LayoutInflater.from(context))
                    alertDialog.setContentView(dialogBinding.root)
                    alertDialog.show()
                }
            }
        }
        viewModel.allWord.observe({ lifecycle }) {

            it?.let { items ->
                if (items.isEmpty()) {
                    _binding?.apply {
                        loadWord.visibility = View.VISIBLE
                        feedRv.visibility = View.GONE
                    }
                } else {
                    listSize = items.size
                    wordAdapter.update(it)
                    _binding?.apply {
                        loadWord.visibility = View.GONE
                        feedRv.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchWord(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun searchWord(word: String) {
        findNavController().navigate(
            R.id.action_navigate_to_navigation_word,
            bundleOf(
                getString(R.string.search_term) to word
            )
        )
    }

    private fun onAudioClicked(word: String) {
        textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            val result = textToSpeech.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
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
        if (textToSpeech != null) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
}