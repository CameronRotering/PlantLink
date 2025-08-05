package com.themakers.plantlink

sealed class Screen(val route: String) {
    object Home : Screen("Home")
    object Settings : Screen("Settings")
    object PlantLinkSettings : Screen("PlantLinkSettings")
    object HistoryPage : Screen("HistoryPage")
    object BluetoothConnect : Screen("BluetoothConnect")
}