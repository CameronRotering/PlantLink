package com.themakers.plantlink.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun BottomToolBar(
    navController: NavHostController
) {
    val currentDestination = navController.currentDestination?.route

    NavigationBar(
        containerColor = Color(226, 114, 91, 255),
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        // Home
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.secondary,
                selectedIconColor = Color(0, 0, 0, 255),
                indicatorColor = MaterialTheme.colorScheme.background
            ),
            selected = currentDestination == "Home",
            onClick = {
               if (currentDestination != "Home") {
                    navController.navigate("Home")
                }
            },
            label = {
                Text(
                    text = "Home",
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
            icon = {
                Icon(
                    imageVector = if (currentDestination == "Home") Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home",
                )
            }
        )

        // Settings
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.secondary,
                selectedIconColor = Color(0, 0, 0, 255),
                indicatorColor = MaterialTheme.colorScheme.background
            ),
            selected = currentDestination == "Settings",
            onClick = {
                if (currentDestination != "Settings") {
                    navController.navigate("Settings")
                }
            },
            label = {
                Text(
                    text = "Settings",
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
            icon = {
                Icon(
                    imageVector = if (currentDestination == "Settings") Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            }
        )
    }
}