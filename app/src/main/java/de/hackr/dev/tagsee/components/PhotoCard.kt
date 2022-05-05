package de.hackr.dev.tagsee.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import de.hackr.dev.tagsee.R
import de.hackr.dev.tagsee.ui.theme.Shapes
import de.hackr.dev.tagsee.model.TaggedPhoto

@Composable
fun PhotoCard(
    photo: TaggedPhoto,
    url: String = photo.url,
    alltags: String,
    titleFontSize: TextUnit = MaterialTheme.typography.h6.fontSize,
    titleFontWeight: FontWeight = FontWeight.Bold,
    shape: Shape = Shapes.medium,
    padding: Dp = 12.dp,
    onTagToggle: (String, String) -> Unit
) {
    var expandedState by remember { mutableStateOf(false) }
    // see https://github.com/stevdza-san/ExpandableCard-JetpackCompose/blob/main/ExpandableCard.kt
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(5.dp, MaterialTheme.colors.primaryVariant.copy(alpha = 0.2f))
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = shape,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier.sizeIn(
                    200.dp, 200.dp, 400.dp, 300.dp
                )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photo.url)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.ic_connection_error),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxSize(),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(6f),
                    text = photo.tags.toSet().joinToString(" "),
                    fontSize = titleFontSize,
                    fontWeight = titleFontWeight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .alpha(ContentAlpha.medium)
                        .rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
            if (expandedState) {
                val tags = alltags.split(",").filter { it.length > 1}
                val scrollState = rememberScrollState()
                Row(Modifier.horizontalScroll(scrollState)) {
                    tags.toSet().forEach { tag ->
                        SelectableTag(
                            selected = photo.tags.contains(tag),
                            text = tag,
                            modifier = Modifier.width(150.dp)) {
                            onTagToggle(url, tag)
                        }
                    }
                }
            }
        }
    }
}