package com.notchdev.vocabpedia.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val term:String,
    val shorDef:String,
)