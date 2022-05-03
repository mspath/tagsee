package de.hackr.dev.tagsee.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.hackr.dev.tagsee.viewmodel.TagseeViewmodel
import de.hackr.dev.tagsee.navigation.Screen
import de.hackr.dev.tagsee.ui.theme.yellow_dark
import de.hackr.dev.tagsee.ui.theme.yellow_light
import de.hackr.dev.tagsee.util.Covers

// TODO error handling on network errors
//  i.e. display a note that galleries or api-service are not available
@Composable
fun LobbyScreen(
    navController: NavHostController,
    viewModel: TagseeViewmodel) {

    val galleryName = viewModel.getGalleryname().collectAsState(initial = "")
    val lobbyGallerySize = viewModel.lobbyGallerySize
    val covers = viewModel.covers

    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colors.background)
    ) {

        Row() {
            Text(
                text = "tagsee",
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.h2
            )
            Text(
                text = "lobby",
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .align(alignment = Alignment.Bottom)
                    .padding(start = 8.dp)
            )
        }

        Text(
            text = "▽ choose a gallery",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .align(alignment = Alignment.Start)
                .padding(start = 0.dp, top = 32.dp, bottom = 0.dp)
        )

        Covers(
            covers = covers.value,
            onSelect = { newName ->
                viewModel.getLobbyGallerySize(newName)
                viewModel.changeGallery(newName) },
            selected = galleryName.value)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {

            if (galleryName.value.isNotEmpty()) {
                Text(
                    "gallery: ${galleryName.value}",
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.caption
                )

                Badge(
                    backgroundColor = MaterialTheme.colors.primaryVariant,
                    contentColor = yellow_light,
                    modifier = Modifier.padding(start = 8.dp)
                        .size(20.dp)
                ) { Text(lobbyGallerySize.value.toString()) }
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .wrapContentSize(align = Alignment.BottomEnd)) {
                Button(
                    onClick = {
                        navController.navigate(Screen.Tags.route)
                    },
                    enabled = lobbyGallerySize.value > 0,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = yellow_light
                    )
                ) {
                    Text(text = "enter")
                }
            }
        }
    }
}
