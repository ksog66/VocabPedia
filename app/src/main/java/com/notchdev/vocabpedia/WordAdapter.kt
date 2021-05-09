package com.notchdev.vocabpedia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.notchdev.vocabpedia.databinding.WordListItemBinding
import com.notchdev.vocabpedia.data.model.Word

class WordAdapter: RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

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
            wordLocalTv.text = word.term
            defLocalTv.text = word.shorDef
        }
    }

    override fun getItemCount(): Int = allWord.size

    fun update(wordList:List<Word>) {
        allWord.clear()
        allWord.addAll(wordList)
        notifyDataSetChanged()
    }
    inner class WordViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}