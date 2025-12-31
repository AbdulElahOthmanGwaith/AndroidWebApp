package com.gwaith.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gwaith.base.ui.MainViewModel
import com.gwaith.base.ui.components.SplashScreen
import com.gwaith.base.ui.screens.HomeScreen
import com.gwaith.base.ui.theme.GwaithBaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GwaithBaseTheme {
                WebApp()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val viewModel: MainViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsState()

        if (uiState.url != uiState.url.lowercase().replace("https://", "").replace("http://", "")) {
            super.onBackPressed()
        } else {
            finish()
        }
    }
}

@Composable
fun WebApp(
    viewModel: MainViewModel = viewModel()
) {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(
            onSplashComplete = {
                showSplash = false
            },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        HomeScreen(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
    }

    BackHandler {
        viewModel.onBackPressed()
    }
}
