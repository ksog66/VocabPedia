package com.notchdev.vocabpedia.data.model


import com.google.gson.annotations.SerializedName

data class Target(
    @SerializedName("tsrc")
    val tsrc: String,
    @SerializedName("tuuid")
    val tuuid: String
)