package com.notchdev.vocabpedia.data.model

data class Quiz(
    var question:String,
    var answer:Int,
    var option:ArrayList<String>
)