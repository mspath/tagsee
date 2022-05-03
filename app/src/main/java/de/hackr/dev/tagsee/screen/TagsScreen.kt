package de.hackr.dev.tagsee.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.hackr.dev.tagsee.navigation.Screen
import de.hackr.dev.tagsee.util.DropdownFilterStrategy
import de.hackr.dev.tagsee.util.FilterStrategy
import de.hackr.dev.tagsee.util.SelectableItem
import de.hackr.dev.tagsee.viewmodel.TagseeViewmodel

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun TagsScreen(
    navController: NavHostController,
    viewModel: TagseeViewmodel) {

    val currentTags = viewModel.getTags().collectAsState(initial = emptySet())
    val selectedFilterStrategy = viewModel.selectedFilterStrategy
    val usertags = viewModel.getUsertags()
    var tags by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf(false) }

    Column(
        Modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState())) {

        Row() {
            Text(
                text = "tagsee",
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.h2
            )

            Text(
                text = "tags",
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .align(alignment = Alignment.Bottom)
                    .padding(start = 8.dp)
            )
        }

        Text(
            text = "▽ choose or add tags",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .align(alignment = Alignment.Start)
                .padding(start = 0.dp, top = 32.dp, bottom = 0.dp)
        )

        Row(
            modifier = Modifier
                .padding(0.dp, 50.dp, 0.dp, 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            DropdownFilterStrategy(onSelect = {index ->
                selectedFilterStrategy.value = FilterStrategy.values()[index]
            })

            Button(
                onClick = { navController.navigate(Screen.Gallery.route) }) {
                Text(text = "enter")
            }
        }

        Text(text = "used tags",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 8.dp))

        currentTags.value.forEach {
            SelectableItem(selected = viewModel.tagSelected(it),
                title = it,
                onClick = { viewModel.toggleSelectedTag(it) },
            modifier = Modifier.background(MaterialTheme.colors.secondary))
        }

        Row() {

            Text(
                text = "add tags",
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(0.dp, 32.dp, 0.dp, 8.dp)
            )

            if (!editing) {
                Icon(Icons.Default.Add, "add",
                    modifier = Modifier
                        .padding(16.dp, 40.dp, 0.dp, 8.dp)
                        .background(MaterialTheme.colors.secondary)
                        .clickable {
                            editing = true
                            tags = viewModel
                                .getUsertags()
                                .joinToString(",")
                            viewModel.saveUsertags("")
                        })
            }
        }

        usertags.filter { !currentTags.value.contains(it) && it.isNotEmpty() }.forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSecondary.copy(alpha = 0.4f),
                modifier = Modifier
                    .border(
                        1.dp,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                        RectangleShape
                    )
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.secondary)
                    .padding(8.dp)
            )
        }

        if (editing) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(value = tags,
                    singleLine = true,
                    label = { Text("tags", style = MaterialTheme.typography.subtitle2) },
                    modifier = Modifier.padding(0.dp, 8.dp),
                    enabled = editing,
                    textStyle = TextStyle.Default.copy(fontSize = 24.sp,
                        color = MaterialTheme.colors.primaryVariant),
                    onValueChange = { newText ->
                        tags = newText
                    })
                Button(onClick = {
                    if (editing) {
                        viewModel.saveUsertags(tags.trim())
                        tags = ""
                    }
                    editing = false
                }) {
                    Text(text = "save")
                }
            }
        }

        // NOTE workaround to offset keyboard
        Box(modifier = Modifier.height(if (editing) 250.dp else 0.dp))
    }
}