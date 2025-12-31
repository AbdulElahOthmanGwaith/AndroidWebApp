package com.gwaith.base.ui

import android.app.Application
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gwaith.base.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WebViewState(
    val url: String = UserPreferences.DEFAULT_HOME_URL,
    val title: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val notificationsEnabled: Boolean = true,
    val javaScriptEnabled: Boolean = true,
    val domStorageEnabled: Boolean = true,
    val showCloseButton: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application.applicationContext)

    private val _uiState = MutableStateFlow(WebViewState())
    val uiState: StateFlow<WebViewState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userPreferences.homeUrl,
                userPreferences.notificationsEnabled,
                userPreferences.javaScriptEnabled,
                userPreferences.domStorageEnabled
            ) { homeUrl, notifications, jsEnabled, domEnabled ->
                _uiState.value.copy(
                    url = homeUrl,
                    notificationsEnabled = notifications,
                    javaScriptEnabled = jsEnabled,
                    domStorageEnabled = domEnabled
                )
            }.collect { newState ->
                _uiState.update { currentState ->
                    if (currentState.url != newState.url && currentState.url != UserPreferences.DEFAULT_HOME_URL) {
                        currentState.copy(
                            notificationsEnabled = newState.notificationsEnabled,
                            javaScriptEnabled = newState.javaScriptEnabled,
                            domStorageEnabled = newState.domStorageEnabled
                        )
                    } else {
                        newState
                    }
                }
            }
        }
    }

    fun updateUrl(url: String) {
        _uiState.update { it.copy(url = url) }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading, error = null) }
    }

    fun setError(error: String) {
        _uiState.update { it.copy(error = error, isLoading = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun retryLoading() {
        clearError()
        _uiState.update { currentState ->
            currentState.copy(isLoading = true)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(enabled)
        }
    }

    fun setJavaScriptEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setJavaScriptEnabled(enabled)
        }
    }

    fun setDomStorageEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDomStorageEnabled(enabled)
        }
    }

    fun setHomeUrl(url: String) {
        viewModelScope.launch {
            userPreferences.setHomeUrl(url)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            val context = getApplication<Application>()
            context.deleteDatabase("webview.db")
            context.deleteDatabase("webviewCache.db")
            WebStorage.getInstance().deleteAllData()
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            WebView.clearCache(true)
        }
    }

    fun exitApp() {
        _uiState.update { it.copy(showCloseButton = false) }
    }

    fun setShowCloseButton(show: Boolean) {
        _uiState.update { it.copy(showCloseButton = show) }
    }

    fun onBackPressed(): Boolean {
        val currentState = _uiState.value
        if (currentState.error != null) {
            clearError()
            return true
        }
        return false
    }
}
