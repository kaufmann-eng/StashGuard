package com.kaufmanneng.stashguard

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kaufmanneng.stashguard.di.appModule
import com.kaufmanneng.stashguard.framework.worker.ExpirationCheckWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.logger.Level
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration
import java.util.concurrent.TimeUnit

@OptIn(KoinExperimentalAPI::class)
class StashGuardApp : Application(), KoinStartup {
    override fun onKoinStartup(): KoinConfiguration {
        return koinConfiguration {
            androidLogger(Level.DEBUG)
            androidContext(this@StashGuardApp)
            modules(appModule)
            workManagerFactory()
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        scheduleExpirationCheck()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Ablauf-Warnungen"
            val descriptionText = "Benachrichtigungen für bald ablaufende Produkte"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("EXPIRATION_CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleExpirationCheck() {
        val workRequest = PeriodicWorkRequestBuilder<ExpirationCheckWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "EXPIRATION_CHECK_WORK",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}