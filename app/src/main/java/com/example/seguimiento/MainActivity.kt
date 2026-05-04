package com.example.seguimiento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.seguimiento.ui.navigation.NavGraph
import com.example.seguimiento.ui.theme.SeguimientoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SeguimientoTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
