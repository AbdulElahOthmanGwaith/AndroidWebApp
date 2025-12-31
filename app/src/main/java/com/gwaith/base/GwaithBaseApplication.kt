package com.gwaith.base

import android.app.Application
import com.gwaith.base.service.NetworkMonitor
import com.gwaith.base.service.NotificationService

/**
 * فئة التطبيق الرئيسية - تهيئة الخدمات العالمية
 * Main Application class - Initialize global services
 */
class GwaithBaseApplication : Application() {

    lateinit var notificationService: NotificationService
        private set

    lateinit var networkMonitor: NetworkMonitor
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize services
        initializeServices()
    }

    private fun initializeServices() {
        notificationService = NotificationService(this)
        networkMonitor = NetworkMonitor(this)
    }

    companion object {
        lateinit var instance: GwaithBaseApplication
            private set

        val notificationService: NotificationService
            get() = instance.notificationService

        val networkMonitor: NetworkMonitor
            get() = instance.networkMonitor
    }
}
