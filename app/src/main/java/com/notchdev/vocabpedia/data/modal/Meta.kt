package com.notchdev.vocabpedia.data.modal


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("ants")
    val ants: List<Any>,
    @SerializedName("id")
    val id: String,
    @SerializedName("offensive")
    val offensive: Boolean,
    @SerializedName("section")
    val section: String,
    @SerializedName("src")
    val src: String,
    @SerializedName("stems")
    val stems: List<String>,
    @SerializedName("syns")
    val syns: List<List<String>>,
    @SerializedName("target")
    val target: Target,
    @SerializedName("uuid")
    val uuid: String
)