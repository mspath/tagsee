package de.hackr.dev.tagsee.navigation

sealed class Screen(val route: String) {
    object Lobby : Screen(route = "lobby")
    object Home : Screen(route = "home")
    object Tags : Screen(route = "tags")
}