package de.hackr.dev.tagsee.screen

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.Constraints
import coil.compose.rememberImagePainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.hackr.dev.tagsee.util.ExpandableCard
import de.hackr.dev.tagsee.viewmodel.TaggedPhoto
import de.hackr.dev.tagsee.viewmodel.TaggedPhotos
import de.hackr.dev.tagsee.viewmodel.TagseeViewmodel

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: TagseeViewmodel
) {

    val username = viewModel.getUsername().collectAsState(initial = "")
    val gallery = viewModel.getGallery().collectAsState(initial = TaggedPhotos(emptyList()))
    val tags = viewModel.getTags().collectAsState(initial = setOf())

    fun toggleTag(url: String, tag: String) {
        viewModel.toggleTag(url, tag)
    }

    Column() {
        Text(text = "gallery ${username.value}",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h2)

        LazyColumn(Modifier.padding(8.dp)) {
            items(gallery.value.collection.size) { index ->
                Box(
                    modifier = Modifier.sizeIn(
                        200.dp, 200.dp, 400.dp, 300.dp
                    )
                ) {
                    Image(
                        painter = rememberImagePainter(
                            data = gallery.value.collection[index].url
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }
                ExpandableCard(
                    photo = gallery.value.collection[index],
                    description = tags.value.joinToString(","),
                    onTagToggle = { x, y -> toggleTag(x, y) }
                )
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
