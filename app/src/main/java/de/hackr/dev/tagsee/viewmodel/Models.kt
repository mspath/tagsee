package de.hackr.dev.tagsee.viewmodel

data class TaggedPhoto(val url: String, val tags: MutableList<String>) {

    // the export format for string based storage like preferences
    // url<tag,tag,..>
    fun serialize() = "${url}<${tags.toSet().map {it.trim()}.joinToString(",")}> "
}

data class TaggedPhotos(val collection: List<TaggedPhoto>) {

    // note: the serialize of single photos will provide the space seperator
    // FIXME move the space here
    fun serialze() = collection
        .filter { it.tags.isNotEmpty() }
        .joinToString("") { it.serialize() }

    // serializes the tagged photos and replaces the status of the pending toggle
    fun serializePendingToggle(url: String, tag: String): String {
        val taggedPhoto = map[url]
        taggedPhoto?.let {
            if (it.tags.contains(tag)) it.tags.remove(tag)
            else it.tags.add(tag)
        }
        return collection.filter { it.tags.isNotEmpty() }
            .joinToString("") { it.serialize() }
    }

    val map = collection.associateBy { it.url }

    fun getTags() = collection.map { it.tags.toList() }.flatten().toSet()

    companion object {
        // we need to pass in both lists of urls and annotated urls since the list
        // of available urls is not in control of the app, files might be new or removed
        // at any time
        // that said this object becomes the source of truth
        fun fromSerialized(gallery: List<String>, meta: List<String>): TaggedPhotos {

            val taggedPhotos = gallery.map { url ->
                TaggedPhoto(url, mutableListOf<String>())
            }

            val map = taggedPhotos.associateBy { it.url }

            meta.forEach { line ->
                val url = line.substringBefore("<")
                val tags = line.substringAfter("<")
                    .substringBefore(">")
                    .split(",")
                map[url]?.tags?.addAll(tags)
            }

            return TaggedPhotos(taggedPhotos)
        }

        val dummy1 = TaggedPhoto("http://192.168.178.41:3000/markus/images/flat1.jpg",
            mutableListOf("puppet"))
        val dummy2 = TaggedPhoto("http://192.168.178.41:3000/markus/images/flat2.jpg",
            mutableListOf("puppet", "yellow"))
        val dummy3 = TaggedPhoto("http://192.168.178.41:3000/markus/images/flat3.jpg",
            mutableListOf("eric"))
        val dummy4 = TaggedPhoto("http://192.168.178.41:3000/markus/images/flat4.jpg",
            mutableListOf("cute"))
        val dummies = TaggedPhotos(listOf(dummy1, dummy2, dummy3, dummy4))
    }
}