package com.gwaith.base.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * خدمة مراقبة الشبكة - تراقب حالة الاتصال بالإنترنت
 * Network Monitor Service - Monitors internet connectivity state
 */
class NetworkMonitor(private val context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * التحقق من حالة الاتصال بالإنترنت
     * Check internet connectivity status
     */
    val isOnline: Boolean
        get() {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }

    /**
     * التحقق من نوع الاتصال
     * Check connection type (WiFi, Cellular, etc.)
     */
    val connectionType: ConnectionType
        get() {
            val network = connectivityManager.activeNetwork ?: return ConnectionType.NONE
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return ConnectionType.NONE

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.CELLULAR
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType.ETHERNET
                else -> ConnectionType.OTHER
            }
        }

    /**
     * مراقبة تغيرات الشبكة كـ Flow
     * Monitor network changes as Flow
     */
    fun observeNetworkState(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                trySend(hasInternet)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Send initial state
        trySend(isOnline)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    /**
     * التحقق من توفر WiFi
     * Check WiFi availability
     */
    val isWifiConnected: Boolean
        get() {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }

    /**
     * التحقق من توفر البيانات المحمولة
     * Check cellular data availability
     */
    val isCellularConnected: Boolean
        get() {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        }
}

/**
 * أنواع الاتصال بالإنترنت
 * Internet connection types
 */
enum class ConnectionType {
    WIFI,
    CELLULAR,
    ETHERNET,
    OTHER,
    NONE
}
