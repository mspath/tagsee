package de.hackr.dev.tagsee.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.hackr.dev.tagsee.viewmodel.TagseeViewmodel
import de.hackr.dev.tagsee.navigation.Screen
import de.hackr.dev.tagsee.viewmodel.TaggedPhotos

@Composable
fun LobbyScreen(navController: NavHostController,
                viewModel: TagseeViewmodel) {

    val usernameState = viewModel.getUsername().collectAsState(initial = "guest")
    val galleryState = viewModel.getGallery().collectAsState(initial = TaggedPhotos(emptyList()))

    val lobbyUserGallerySize = viewModel.lobbyUserGallerySize

    // textfield
    var name by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .padding(8.dp)
        .verticalScroll(rememberScrollState())) {

        Text(text = "tagsee",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h2)

        TextField(value = name,
            singleLine = true,
            label = { Text("username") },
            onValueChange = { newText -> name = newText } )

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {  viewModel.getLobbyUserGallerySize(name) }) {
                Text(text = "check")
            }
            Text(lobbyUserGallerySize.value.toString())

            Button(onClick = {  navController.navigate(Screen.Tags.route) }) {
                Text(text = "enter")
            }
        }

        Button(onClick = { viewModel.changeUser(name) }) {
            Text(text = "change User")
        }

        // DEV *******************************************

        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(color = Color.DarkGray))
        Text("dev")

        Spacer(modifier = Modifier.height(10.dp))
        Text("currently applied name:")
        Text(usernameState.value)

        Text(galleryState.toString())

        Button(onClick = { viewModel.setupUser() }) {
            Text(text = "setup User")
        }
    }
}

