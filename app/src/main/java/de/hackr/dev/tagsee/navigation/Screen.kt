package de.hackr.dev.tagsee.navigation

sealed class Screen(val route: String) {
    object Lobby : Screen(route = "lobby")
    object Gallery : Screen(route = "gallery")
    object Tags : Screen(route = "tags")
}