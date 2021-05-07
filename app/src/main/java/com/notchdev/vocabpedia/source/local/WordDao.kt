package com.notchdev.vocabpedia.source.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word:Word)

    @Delete
    suspend fun delete(word:Word)

    @Query("Select * from words order by id DESC")
    fun getAllWord() : LiveData<List<Word>>
}