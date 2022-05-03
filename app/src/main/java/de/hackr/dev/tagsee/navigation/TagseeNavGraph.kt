package de.hackr.dev.tagsee.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.hackr.dev.tagsee.screen.GalleryScreen
import de.hackr.dev.tagsee.screen.LobbyScreen
import de.hackr.dev.tagsee.screen.TagsScreen
import de.hackr.dev.tagsee.viewmodel.TagseeViewmodel

@Composable
fun TagseeNavGraph(
    navController: NavHostController,
    startDestination: Screen,
    viewModel: TagseeViewmodel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route
    ) {
        composable(route = Screen.Lobby.route) {
            LobbyScreen(navController = navController, viewModel)
        }
        composable(route = Screen.Gallery.route) {
            GalleryScreen(navController = navController, viewModel)
        }
        composable(route = Screen.Tags.route) {
            TagsScreen(navController = navController, viewModel)
        }
    }
}
