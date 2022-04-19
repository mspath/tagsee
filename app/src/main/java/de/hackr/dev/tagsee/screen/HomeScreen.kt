package de.hackr.dev.tagsee.screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.hackr.dev.tagsee.util.PhotoCard
import de.hackr.dev.tagsee.util.FilterStrategy
import de.hackr.dev.tagsee.viewmodel.TaggedPhoto
import de.hackr.dev.tagsee.viewmodel.TaggedPhotos
import de.hackr.dev.tagsee.viewmodel.TagseeViewmodel

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: TagseeViewmodel
) {
    val username = viewModel.getUsername().collectAsState(initial = "")
    val gallery = viewModel.getGallery().collectAsState(initial = TaggedPhotos(emptyList()))
    val tags = viewModel.getTags().collectAsState(initial = setOf())
    val usertags = viewModel.getUsertags()
    val alltags = tags.value.toList() + usertags.toList()
    val filterStrategy = viewModel.selectedFilterStrategy
    val includeList = viewModel.selectedTagsMap.filter { it.value }.keys

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

    Column() {
        Text(text = "gallery ${username.value}",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h2)

        Text(text = "strategy ${filterStrategy.value}",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h4)

        Text(text = "tagsselected ${viewModel.selectedTagsMap.filter { it.value }.keys}",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h6)

        LazyColumn(Modifier.padding(8.dp)) {
            items(gallery.value.collection.size, key = { index ->
                gallery.value.collection[index].url
            }) { index ->
                if (isIncluded(gallery.value.collection[index])) {
                    PhotoCard(
                        photo = gallery.value.collection[index],
                        alltags = alltags.joinToString(","),
                        onTagToggle = { url, tag -> toggleTag(url, tag) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGrid(photos: List<TaggedPhoto>) {
    LazyVerticalGrid(cells = GridCells.Fixed(2)) {
        items(photos.size) { index ->
            Box(modifier = Modifier.size(200.dp)) {
                Image(
                    painter = rememberImagePainter(
                        data = photos[index].url
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
}
