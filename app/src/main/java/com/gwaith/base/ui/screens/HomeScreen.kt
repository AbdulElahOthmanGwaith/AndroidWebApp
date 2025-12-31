package com.gwaith.base.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gwaith.base.R
import com.gwaith.base.ui.MainViewModel
import com.gwaith.base.ui.components.WebViewComponent
import com.gwaith.base.ui.screens.components.ErrorView
import com.gwaith.base.ui.screens.components.SettingsContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = uiState.title.ifEmpty { stringResource(R.string.app_name) },
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    AnimatedVisibility(
                        visible = uiState.showCloseButton,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = { viewModel.exitApp() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.retryLoading() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.error != null -> {
                    ErrorView(
                        error = uiState.error!!,
                        onRetry = { viewModel.retryLoading() }
                    )
                }
                else -> {
                    WebViewComponent(
                        url = uiState.url,
                        onLoadingStateChanged = { isLoading ->
                            viewModel.setLoading(isLoading)
                        },
                        onUrlChanged = { url ->
                            viewModel.updateUrl(url)
                        },
                        onTitleChanged = { title ->
                            viewModel.updateTitle(title)
                        },
                        onError = { error ->
                            viewModel.setError(error)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                SettingsContent(
                    viewModel = viewModel,
                    onDismiss = {
                        scope.launch {
                            sheetState.hide()
                            showBottomSheet = false
                        }
                    }
                )
            }
        }
    }
}
