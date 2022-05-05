package de.hackr.dev.tagsee.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import de.hackr.dev.tagsee.R
import de.hackr.dev.tagsee.ui.theme.yellow_light
import de.hackr.dev.tagsee.model.Cover

@Composable
fun Covers(covers: List<Cover>, onSelect: (String) -> Unit, selected: String?) {

    LazyRow(
        Modifier
            .padding(0.dp, 24.dp, 0.dp, 0.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(covers) { cover ->
            val alpha = if (cover.name == selected) 0.8f else 0.3f
            Box(
                Modifier
                    .width(200.dp)
                    .height(200.dp)
                    .background(MaterialTheme.colors.primary.copy(alpha))
                    .clickable { onSelect(cover.name) }) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(cover.imageRight)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.ic_connection_error),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp, 8.dp, 90.dp, 32.dp)
                        .border(6.dp, color = yellow_light),
                    contentScale = ContentScale.FillBounds
                )
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(cover.imageLeft)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.ic_connection_error),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(90.dp, 32.dp, 8.dp, 8.dp)
                        .border(6.dp, color = yellow_light),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
}