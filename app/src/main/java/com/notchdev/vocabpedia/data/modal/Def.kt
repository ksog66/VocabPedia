package com.notchdev.vocabpedia.data.modal


import com.google.gson.annotations.SerializedName

data class Def(
    @SerializedName("sseq")
    val sseq: List<List<List<Any>>>
)