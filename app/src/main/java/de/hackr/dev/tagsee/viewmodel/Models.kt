package de.hackr.dev.tagsee.viewmodel

data class TaggedPhoto(val url: String, val tags: MutableList<String>) {

    // the export format for string based storage like preferences
    // url<tag,tag,..>
    fun serialize() = "${url}<${tags.toSet().map {it.trim()}.joinToString(",")}>"

    companion object {
        fun fromSerializedString(line: String): TaggedPhoto {
            val url = line.substringBefore("<")
            val tags = line.substringAfter("<")
                .substringBefore(">")
                .split(",")
            return TaggedPhoto(url, tags.toMutableList())
        }
    }
}

data class TaggedPhotos(val collection: List<TaggedPhoto>) {

    // use this to save the current state of a collection
    fun serialze() = collection
        .filter { it.tags.isNotEmpty() }
        .joinToString(" ") { it.serialize() }
        .trim()

    // serializes the tagged photos and replaces the status of the pending toggle
    // use this to update values in a collection
    fun serializePendingToggle(url: String, tag: String): String {
        val taggedPhoto = map[url]
        taggedPhoto?.let {
            if (it.tags.contains(tag)) it.tags.remove(tag)
            else it.tags.add(tag)
        }
        return collection
            .filter { it.tags.isNotEmpty() }
            .joinToString(" ") { it.serialize() }
            .trim()
    }

    private val map = collection.associateBy { it.url }

    fun getTags() = collection.map { it.tags.toList() }.flatten().toSet()

    companion object {
        // we need to pass in both lists of urls and tagged urls since the list
        // of available urls is not in control of the app, files might be new or removed
        // at any time
        // that said this object becomes the source of truth
        fun fromSerialized(gallery: List<String>, meta: List<String>): TaggedPhotos {

            // map of the available metadata
            val metaMap = meta.associate { line ->
                Pair(line.substringBefore("<"), TaggedPhoto.fromSerializedString(line))
            }

            // we just use those which are currently available
            // defaults to no tags if there are no metadata yet.
            val taggedPhotos = gallery.map { url ->
                metaMap.getOrDefault(url, TaggedPhoto(url, mutableListOf<String>()))
            }

            return TaggedPhotos(taggedPhotos)
        }

        // FIXME move this in a helper class
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