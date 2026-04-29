package com.example.darepack_complete

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.darepack_complete.navigation.AppNavHost
import com.example.darepack_complete.ui.theme.DarePackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DarePackTheme {
                AppNavHost()
            }
        }
    }
}
