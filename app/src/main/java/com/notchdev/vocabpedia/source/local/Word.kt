package com.notchdev.vocabpedia.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val term:String,
    val shorDef:String,
    val example:String
)