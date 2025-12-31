package com.gwaith.base.util

import android.content.Context
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 * دوال الإضافة المفيدة لـ Views
 * Useful View extension functions
 */

/**
 * إظهار العنصر
 * Show view
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * إخفاء العنصر
 * Hide view
 */
fun View.hide() {
    visibility = View.GONE
}

/**
 * جعل العنصر غير مرئي (يشغل مساحة)
 * Make view invisible (still takes space)
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * تبديل الإظهار/الإخفاء
 * Toggle show/hide
 */
fun View.toggleVisibility() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

/**
 * إظهار رسالة Toast قصيرة
 * Show short toast message
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * إظهار رسالة Toast طويلة
 * Show long toast message
 */
fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * إظهار Snackbar
 * Show snackbar
 */
fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

/**
 * إظهار Snackbar مع إجراء
 * Show snackbar with action
 */
fun View.showSnackbarWithAction(
    message: String,
    actionText: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: () -> Unit
) {
    Snackbar.make(this, message, duration)
        .setAction(actionText) { action() }
        .show()
}

/**
 * دوال WebView الإضافية
 * WebView extension functions
 */

/**
 * مسح بيانات WebView
 * Clear WebView data
 */
fun WebView.clearAllData() {
    clearHistory()
    clearCache(true)
    clearFormData()
    clearSslPreferences()
}

/**
 * إعادة تحميل مع تجاهل التخزين المؤقت
 * Reload ignoring cache
 */
fun WebView.reloadIgnoreCache() {
    settings.cacheMode = WebView.LOAD_NO_CACHE
    reload()
    settings.cacheMode = WebView.LOAD_DEFAULT
}

/**
 * تحميل URL مع تجاهل التخزين المؤقت
 * Load URL ignoring cache
 */
fun WebView.loadUrlIgnoreCache(url: String) {
    settings.cacheMode = WebView.LOAD_NO_CACHE
    loadUrl(url)
    settings.cacheMode = WebView.LOAD_DEFAULT
}
