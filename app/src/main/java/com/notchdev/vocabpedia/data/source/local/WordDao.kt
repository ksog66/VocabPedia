package com.notchdev.vocabpedia.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.notchdev.vocabpedia.data.model.Word

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word)

    @Query("Delete from words where id=:id")
    suspend fun delete(id:Long)

    @Query("Select * from words order by id DESC")
    fun getAllWord() : LiveData<List<Word>>
}