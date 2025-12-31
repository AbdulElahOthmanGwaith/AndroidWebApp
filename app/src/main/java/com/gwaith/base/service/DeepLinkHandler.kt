package com.gwaith.base.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import com.gwaith.base.ui.MainViewModel

/**
 * معالج الروابط العميقة - يعالج الروابط الخارجية والتطبيقات الأخرى
 * Deep Link Handler - Handles external links and other apps
 */
class DeepLinkHandler(private val context: Context) {

    /**
     * معالجة رابط خارجي
     * Handle external link
     */
    fun handleExternalLink(url: String): Boolean {
        val uri = Uri.parse(url)

        return when (uri.scheme?.lowercase()) {
            "http", "https" -> {
                // Open in external browser
                openInBrowser(url)
                true
            }
            "mailto" -> {
                // Open email client
                openEmailClient(url)
                true
            }
            "tel" -> {
                // Open dialer
                openDialer(url)
                true
            }
            "sms" -> {
                // Open messaging app
                openMessaging(url)
                true
            }
            "geo" -> {
                // Open maps
                openMaps(url)
                true
            }
            "market" -> {
                // Open Play Store
                openPlayStore(url)
                true
            }
            else -> false
        }
    }

    /**
     * فتح في المتصفح الخارجي
     * Open in external browser
     */
    private fun openInBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * فتح عميل البريد
     * Open email client
     */
    private fun openEmailClient(url: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * فتح برنامج الاتصال
     * Open dialer
     */
    private fun openDialer(url: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *打开 المراسلات
     * Open messaging
     */
    private fun openMessaging(url: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *打开 الخرائط
     * Open maps
     */
    private fun openMaps(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                setPackage("com.google.android.apps.maps")
            }

            // Try Google Maps first, then any maps app
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback to any maps app
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *打开 متجر Play
     * Open Play Store
     */
    private fun openPlayStore(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                setPackage("com.android.vending")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to browser
            openInBrowser(url)
        }
    }

    /**
     * التحقق من صحة الرابط
     * Validate URL
     */
    fun isValidUrl(url: String): Boolean {
        return try {
            val uri = Uri.parse(url)
            uri.scheme != null && uri.host != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * إضافة بروتوكول http إذا كان مفقوداً
     * Add http protocol if missing
     */
    fun ensureHttpProtocol(url: String): String {
        return if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else {
            url
        }
    }
}
