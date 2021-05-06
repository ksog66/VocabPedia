package com.notchdev.vocabpedia.source.api.modal


import com.google.gson.annotations.SerializedName

data class Def(
    @SerializedName("sseq")
    val sseq: List<List<List<Any>>>
)