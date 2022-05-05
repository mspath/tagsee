package de.hackr.dev.tagsee.model

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

    @Transient
    private val map = collection.associateBy { it.url }

    fun getTags() = collection.map { it.tags.toList() }.flatten().toSet()

    fun toJson() = TaggedPhoto.gson.toJson(this)

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

            // we only take those which are currently available
            // defaults to no tags if there are no metadata yet.
            val taggedPhotos = gallery.map { url ->
                metaMap.getOrDefault(url, TaggedPhoto(url, mutableListOf<String>()))
            }

            return TaggedPhotos(taggedPhotos)
        }

        fun fromJson(json: String): TaggedPhotos {
            val gson = TaggedPhoto.gson
            return gson.fromJson(json, TaggedPhotos::class.java)
        }
    }
}
