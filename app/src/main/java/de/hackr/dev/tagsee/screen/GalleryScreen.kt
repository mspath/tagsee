package de.hackr.dev.tagsee.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.hackr.dev.tagsee.model.TaggedPhoto
import de.hackr.dev.tagsee.model.TaggedPhotos
import de.hackr.dev.tagsee.components.PhotoCard
import de.hackr.dev.tagsee.components.FilterStrategy
import de.hackr.dev.tagsee.viewmodel.TagseeViewmodel

@Composable
fun GalleryScreen(
    navController: NavHostController,
    viewModel: TagseeViewmodel
) {
    val galleryname = viewModel.getGalleryname().collectAsState(initial = "")
    val gallery = viewModel.getGallery().collectAsState(initial = TaggedPhotos(emptyList()))
    val tags = viewModel.getTags().collectAsState(initial = setOf())
    val usertags = viewModel.getUsertags()
    val alltags = tags.value.toList() + usertags.toList()
    val filterStrategy = viewModel.selectedFilterStrategy
    val includeList = viewModel.selectedTagsMap.filter { it.value }.keys

    // should an image be displayed based on the selected filterStrategy
    fun isIncluded(taggedPhoto: TaggedPhoto): Boolean {
        return when (filterStrategy.value) {
            FilterStrategy.ALL_IMAGES -> true
            FilterStrategy.NO_TAG -> taggedPhoto.tags.isNullOrEmpty()
            FilterStrategy.ANY_TAG -> taggedPhoto.tags.any { it in includeList }
            FilterStrategy.ALL_TAGS -> includeList.all { it in taggedPhoto.tags }
        }
    }

    fun toggleTag(url: String, tag: String) {
        viewModel.toggleTag(url, tag)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item {
            Row() {
                Text(
                    text = "tagsee",
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.h2
                )

                Text(
                    text = "gallery",
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier
                        .align(alignment = Alignment.Bottom)
                        .padding(start = 8.dp)
                )
            }
        }

        item {
            Text(
                text = "▽ ${galleryname.value}",
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .padding(start = 0.dp, top = 32.dp, bottom = 0.dp)
            )
        }

        item { Spacer(modifier = Modifier.height(48.dp)) }

        items(gallery.value.collection) { taggedPhoto ->
            if (isIncluded(taggedPhoto)) {
                PhotoCard(
                    photo = taggedPhoto,
                    alltags = alltags.joinToString(","),
                    onTagToggle = { url, tag -> toggleTag(url, tag) }
                )
            }
        }
    }
}