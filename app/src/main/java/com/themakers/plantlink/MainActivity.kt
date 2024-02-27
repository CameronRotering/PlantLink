package com.themakers.plantlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.themakers.plantlink.HistoryPage.HistoryPage
import com.themakers.plantlink.MainPage.MainPage
import com.themakers.plantlink.SettingsPage.SettingsPage
import com.themakers.plantlink.ui.theme.PlantLInkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlantLInkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "Home"
                    ) {
                        composable("Home") {
                            MainPage(
                                context = applicationContext,
                                navController = navController
                            )
                        }

                        composable("Settings") {
                            SettingsPage(
                                navController = navController,
                                context = applicationContext
                            )
                        }

                        composable("History") {
                            HistoryPage(
                                navController = navController,
                                context = applicationContext
                            )
                        }
                    }
                }
            }
        }
    }
}