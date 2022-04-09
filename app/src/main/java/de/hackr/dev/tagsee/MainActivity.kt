package de.hackr.dev.tagsee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import de.hackr.dev.tagsee.navigation.TagseeNavGraph
import de.hackr.dev.tagsee.ui.theme.TagseeTheme
import de.hackr.dev.tagsee.navigation.Screen
import de.hackr.dev.tagsee.viewmodel.TagseeViewmodel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TagseeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val viewModel: TagseeViewmodel = hiltViewModel()
                    val startDestination = Screen.Lobby
                    val navController = rememberNavController()
                    TagseeNavGraph(navController = navController,
                        startDestination = startDestination,
                        viewModel)
                }
            }
        }
    }
}
