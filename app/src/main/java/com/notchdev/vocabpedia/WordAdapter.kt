package com.notchdev.vocabpedia

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.notchdev.vocabpedia.databinding.WordListItemBinding
import com.notchdev.vocabpedia.data.model.Word
import java.util.*
import kotlin.collections.ArrayList

class WordAdapter(
    private val onAudioClicked: (word:String) -> Unit
): RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    private val allWord = ArrayList<Word>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder(
            parent.context.getSystemService(LayoutInflater::class.java).inflate(
                R.layout.word_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        WordListItemBinding.bind(holder.itemView).apply {
            val word = allWord[position]
            wordLocalTv.text = word.term.capitalize(Locale.ROOT)
            defLocalTv.text = word.shorDef
            wordLocalTv.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if(event?.action == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= wordLocalTv.right - wordLocalTv.totalPaddingEnd) {
                            onAudioClicked(wordLocalTv.text.toString())
                            return true
                        }
                    }
                    return true
                }
            })
        }
    }

    override fun getItemId(position: Int): Long {
        return allWord[position].id
    }

    override fun getItemCount(): Int = allWord.size

    fun update(wordList:List<Word>) {
        allWord.clear()
        allWord.addAll(wordList)
        notifyDataSetChanged()
    }
    inner class WordViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}