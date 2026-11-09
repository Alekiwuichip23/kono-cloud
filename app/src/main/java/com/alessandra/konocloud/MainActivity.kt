package com.alessandra.konocloud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alessandra.konocloud.presentation.navigation.AppNavGraph
import com.alessandra.konocloud.ui.theme.KonoCloudTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KonoCloudTheme {
                AppNavGraph()
            }
        }
    }
}
