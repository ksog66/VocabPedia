package com.notchdev.vocabpedia.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.notchdev.vocabpedia.data.model.Word

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word)

    @Delete
    suspend fun delete(word: Word)

    @Query("Select * from words order by id DESC")
    fun getAllWord() : LiveData<List<Word>>
}