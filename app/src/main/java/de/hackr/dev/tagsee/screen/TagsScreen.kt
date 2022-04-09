package de.hackr.dev.tagsee.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import de.hackr.dev.tagsee.navigation.Screen
import de.hackr.dev.tagsee.util.SelectableItem
import de.hackr.dev.tagsee.viewmodel.TagseeViewmodel

@Composable
fun TagsScreen(navController: NavHostController, viewModel: TagseeViewmodel) {

    var selected by remember { mutableStateOf(false) }

    val currentTags = viewModel.getTags().collectAsState(initial = emptySet())

    Column(Modifier.padding(8.dp)) {
        Button(onClick = { navController.navigate(Screen.Home.route) }) {
            Text(text = "Enter")
        }

        currentTags.value.forEach {
            SelectableItem(selected = selected,
                title = it,
                onClick = { selected = !selected })
        }

        SelectableItem(selected = selected,
            title = "Catsar",
            onClick = { selected = !selected })

        SelectableItem(selected = selected,
            title = "Cat",
            onClick = { selected = !selected })

        SelectableItem(selected = selected,
            title = "Dog",
            onClick = { selected = !selected })

        SelectableItem(selected = selected,
            title = "Catsar",
            onClick = { selected = !selected })

        SelectableItem(selected = selected,
            title = "Cat",
            onClick = { selected = !selected })

        SelectableItem(selected = selected,
            title = "Dog",
            onClick = { selected = !selected })
    }
}