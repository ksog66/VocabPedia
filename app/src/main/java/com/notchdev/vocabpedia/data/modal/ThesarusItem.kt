package com.notchdev.vocabpedia.data.modal


import com.google.gson.annotations.SerializedName

data class ThesarusItem(
    @SerializedName("def")
    val def: List<Def>,
    @SerializedName("fl")
    val fl: String,
    @SerializedName("hwi")
    val hwi: Hwi,
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("shortdef")
    val shortdef: List<String>,
    @SerializedName("sls")
    val sls: List<String>
)