package com.notchdev.vocabpedia.source.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object VocabClient {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://dictionaryapi.com/api/v3/references/thesaurus/json/")
        .addConverterFactory(GsonConverterFactory.create())

    val vocabAPI = retrofit
        .build()
        .create(VocabAPI::class.java)
}