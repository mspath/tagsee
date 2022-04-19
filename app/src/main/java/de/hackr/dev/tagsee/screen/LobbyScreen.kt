package de.hackr.dev.tagsee.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.hackr.dev.tagsee.viewmodel.TagseeViewmodel
import de.hackr.dev.tagsee.navigation.Screen

@Composable
fun LobbyScreen(
    navController: NavHostController,
    viewModel: TagseeViewmodel) {

    // textfield
    var name by remember { mutableStateOf("flat") }
    var changed by remember { mutableStateOf(true) }

    val lobbyUserGallerySize = viewModel.lobbyUserGallerySize

    Column(modifier = Modifier
        .padding(8.dp)
        .verticalScroll(rememberScrollState())) {

        Text(text = "tagsee",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h2)

        Text(text = "lobby",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h4)

        TextField(value = name,
            singleLine = true,
            label = { Text("username") },
            modifier = Modifier.padding(0.dp, 16.dp),
            onValueChange = {
                    newText -> name = newText
                    changed = true })

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                viewModel.getLobbyUserGallerySize(name)
                changed = false
            }) {
                Text(text = "check gallery")
            }
            if (!changed) Text(lobbyUserGallerySize.value.toString())
            Button(onClick = {
                viewModel.changeUser(name)
                navController.navigate(Screen.Tags.route) },
                enabled = lobbyUserGallerySize.value > 0 && !changed
                ) {
                Text(text = "enter")
            }
        }

    }
}




