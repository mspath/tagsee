package de.hackr.dev.tagsee.model

import com.google.gson.Gson

data class TaggedPhoto(val url: String, val tags: MutableList<String>) {

    // the export format for string based storage like preferences
    // url<tag,tag,..>
    fun serialize() = "${url}<${tags.toSet().map {it.trim()}.joinToString(",")}>"

    fun toJson() = gson.toJson(this)

    companion object {
        fun fromSerializedString(line: String): TaggedPhoto {
            val url = line.substringBefore("<")
            val tags = line.substringAfter("<")
                .substringBefore(">")
                .split(",")
            return TaggedPhoto(url, tags.toMutableList())
        }

        val gson = Gson()
        fun fromJson(json: String) = gson.fromJson(json, TaggedPhoto::class.java)
    }
}