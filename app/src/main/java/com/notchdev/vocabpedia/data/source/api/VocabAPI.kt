package com.notchdev.vocabpedia.data.source.api

import com.notchdev.vocabpedia.BuildConfig
import com.notchdev.vocabpedia.data.modal.Thesarus
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VocabAPI {

    @GET("{term}")
    suspend fun getMeaning(
        @Path("term") word:String,
        @Query("key")
        apiKey:String = BuildConfig.API_KEY,
    ): Response<Thesarus>
}