package de.hackr.dev.tagsee.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class FilterStrategy(val value: String) {

    ALL_IMAGES("all images"),
    ANY_TAG("any tag"),
    ALL_TAGS("all tags"),
    NO_TAG("untagged"),

}

@Composable
fun DropdownFilterStrategy(onSelect: (index: Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val items = FilterStrategy.values()
    var selectedIndex by remember { mutableStateOf(0) }
    Box(modifier = Modifier
        .fillMaxSize(0.5f)
        .wrapContentSize(Alignment.TopStart)
        .border(2.dp,
            MaterialTheme.colors.contentColorFor(MaterialTheme.colors.background),
            shape = RoundedCornerShape(16.dp)
        )) {
        Text(items[selectedIndex].value,modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { expanded = true })
            .background(
                MaterialTheme.colors.background
            )
            .padding(8.dp))
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Drop-Down Arrow",
            modifier = Modifier.padding(8.dp).
            align(Alignment.CenterEnd)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.8F)
                .background(
                    MaterialTheme.colors.background
                )
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    onSelect(index)
                }) {
                    Text(text = s.value)
                }
            }
        }
    }
}