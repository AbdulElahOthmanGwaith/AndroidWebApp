package com.gwaith.base.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.gwaith.base.data.UserPreferences
import kotlinx.coroutines.flow.first

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewComponent(
    modifier: Modifier = Modifier,
    url: String,
    onLoadingStateChanged: (Boolean) -> Unit,
    onUrlChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit = {},
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    var progress by remember { mutableStateOf(0f) }
    var isLoading by remember { mutableStateOf(true) }
    var webView: WebView? by remember { mutableStateOf(null) }

    val webViewClient = remember {
        object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                isLoading = true
                onLoadingStateChanged(true)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isLoading = false
                onLoadingStateChanged(false)
                url?.let { onUrlChanged(it) }
                view?.title?.let { onTitleChanged(it) }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString() ?: return false
                return !url.startsWith("http://") && !url.startsWith("https://")
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
            }
        }
    }

    val webChromeClient = remember {
        object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progress = newProgress / 100f
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                title?.let { onTitleChanged(it) }
            }
        }
    }

    LaunchedEffect(url) {
        val jsEnabled = userPreferences.javaScriptEnabled.first()
        val domEnabled = userPreferences.domStorageEnabled.first()

        webView?.let { webView ->
            webView.settings.javaScriptEnabled = jsEnabled
            webView.settings.domStorageEnabled = domEnabled
            webView.loadUrl(url)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading && progress < 1f) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                        allowFileAccess = true
                        allowContentAccess = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        databaseEnabled = true
                        setSupportMultipleWindows(true)
                        javaScriptCanOpenWindowsAutomatically = true

                        userAgentString = "$userAgentString GwaithBase/1.0"
                    }

                    webViewClient = this@remember.webViewClient
                    webChromeClient = this@remember.webChromeClient

                    webView = this
                    loadUrl(url)
                }
            },
            update = { webView ->
                if (webView.url != url) {
                    webView.loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            webView?.let { webView ->
                webView.stopLoading()
                webView.clearHistory()
                webView.clearCache(true)
                webView.clearFormData()
                webView.destroy()
            }
        }
    }
}
