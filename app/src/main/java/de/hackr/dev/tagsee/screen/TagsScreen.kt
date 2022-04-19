package de.hackr.dev.tagsee.screen

import android.util.Log
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import de.hackr.dev.tagsee.navigation.Screen
import de.hackr.dev.tagsee.util.DropdownFilterStrategy
import de.hackr.dev.tagsee.util.FilterStrategy
import de.hackr.dev.tagsee.util.SelectableItem
import de.hackr.dev.tagsee.viewmodel.TagseeViewmodel

@Composable
fun TagsScreen(navController: NavHostController, viewModel: TagseeViewmodel) {

    val currentTags = viewModel.getTags().collectAsState(initial = emptySet())
    val selectedFilterStrategy = viewModel.selectedFilterStrategy

    val usertags = viewModel.getUsertags()

    var tags by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf(false) }

    Column(
        Modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState())) {

        Text(text = "tagsee",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h2)

        Text(text = "tags",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h4)

        Button(onClick = { navController.navigate(Screen.Home.route) }) {
            Text(text = "Enter")
        }

        DropdownFilterStrategy(onSelect = {index ->
            selectedFilterStrategy.value = FilterStrategy.values()[index]
        })

        Text(text = "applied tags",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h4)

        currentTags.value.forEach {
            SelectableItem(selected = viewModel.tagSelected(it),
                title = it,
                onClick = { viewModel.toggleSelectedTag(it) })
        }

        Text(text = "new tags",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h4)

        usertags.filter { !currentTags.value.contains(it) && it.isNotEmpty() }.forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .border(4.dp, color = MaterialTheme.colors.primary, RectangleShape)
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .padding(12.dp)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()) {
            TextField(value = tags,
                singleLine = true,
                label = { Text("tags (comma seperated)") },
                modifier = Modifier.padding(0.dp, 16.dp),
                enabled = editing,
                onValueChange = { newText ->
                    tags = newText
                })
            Button(onClick = {
                if (editing) {
                    viewModel.saveUsertags(tags.trim())
                    tags = ""
                }
                editing = !editing
            }) {
                Text(text = if (editing) "save" else "edit")
            }
        }

        // NOTE workaround to offset keyboard
        Box(modifier = Modifier.height(if (editing) 250.dp else 0.dp))
    }

//    Box (modifier = Modifier.width(100.dp).height(100.dp).
//    background(Color.Red.copy(alpha = 0.5f))) {
//
//    }
}
