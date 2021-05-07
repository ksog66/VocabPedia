package com.notchdev.vocabpedia.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Word::class),version = 1, exportSchema = true)
abstract class WordDatabase: RoomDatabase() {

    abstract fun getWordDao(): WordDao

    companion object {

        private var INSTANCE: WordDatabase? = null

        fun getDatabase(context: Context) : WordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "word.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}