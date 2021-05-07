package com.notchdev.vocabpedia.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Word::class),version = 1, exportSchema = true)
abstract class WordDatabase: RoomDatabase() {

    abstract fun getWordDao(): WordDao

    companion object {

        @Volatile
        private var instance: WordDatabase? = null

        private val LOCK= Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also{ instance =it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                WordDatabase::class.java,
                "word.db"
            ).build()
    }
}