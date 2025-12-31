package com.gwaith.base.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * أدوات التطبيق - وظائف مساعدة عامة
 * App Utils - General helper functions
 */
object AppUtils {

    /**
     * الحصول على إصدار التطبيق
     * Get app version
     */
    fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.versionName ?: "1.0"
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionName ?: "1.0"
            }
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0"
        }
    }

    /**
     * الحصول على إصدار التطبيق (رقم)
     * Get app version code
     */
    fun getAppVersionCode(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            1L
        }
    }

    /**
     * التحقق من إصدار Android
     * Check Android version
     */
    fun isAndroidVersionOrHigher(versionCode: Int): Boolean {
        return Build.VERSION.SDK_INT >= versionCode
    }

    /**
     * التحقق من Oreo أو أعلى
     * Check Oreo or higher
     */
    fun isOreoOrHigher(): Boolean = isAndroidVersionOrHigher(Build.VERSION_CODES.O)

    /**
     * التحقق من Pie أو أعلى
     * Check Pie or higher
     */
    fun isPieOrHigher(): Boolean = isAndroidVersionOrHigher(Build.VERSION_CODES.P)

    /**
     * التحقق من Android 10 أو أعلى
     * Check Android 10 or higher
     */
    fun isAndroid10OrHigher(): Boolean = isAndroidVersionOrAndroid(10)

    private fun isAndroidVersionOrAndroid(version: Int): Boolean {
        return Build.VERSION.SDK_INT >= version
    }

    /**
     * تنسيق حجم الملف
     * Format file size
     */
    fun formatFileSize(sizeInBytes: Long): String {
        return when {
            sizeInBytes < 1024 -> "$sizeInBytes B"
            sizeInBytes < 1024 * 1024 -> String.format("%.1f KB", sizeInBytes / 1024.0)
            sizeInBytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", sizeInBytes / (1024.0 * 1024.0))
            else -> String.format("%.1f GB", sizeInBytes / (1024.0 * 1024.0 * 1024.0))
        }
    }

    /**
     * التحقق من وجود اتصال بالإنترنت
     * Check internet connectivity
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

/**
 * دوال الإضافة لـ Context
 * Extension functions for Context
 */
object ContextExtensions {

    /**
     * الحصول على اسم التطبيق
     * Get app name
     */
    fun Context.getAppName(): String {
        return applicationInfo.loadLabel(packageManager).toString()
    }

    /**
     * التحقق من تثبيت تطبيق
     * Check if app is installed
     */
    fun Context.isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
