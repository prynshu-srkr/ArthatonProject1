package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.KavachDatabase
import com.example.data.ScanRepository
import com.example.ui.KavachPayScreen
import com.example.ui.KavachViewModel
import com.example.ui.theme.KavachPayTheme
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        val database = KavachDatabase.getInstance(applicationContext)
        val repository = ScanRepository(database.scanHistoryDao())
        val viewModelFactory = KavachViewModel.Factory(repository, applicationContext)

        // STEP 2: Initialize UI
        setContent {
            val kavachViewModel: KavachViewModel = viewModel(factory = viewModelFactory)
            val uiState by kavachViewModel.uiState.collectAsStateWithLifecycle()

            KavachPayTheme(darkTheme = uiState.isDarkMode) {
                KavachPayScreen(viewModel = kavachViewModel)
            }
        }
    }
}
