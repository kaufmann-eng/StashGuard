package com.kaufmanneng.stashguard.framework.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kaufmanneng.stashguard.MainActivity
import com.kaufmanneng.stashguard.R
import com.kaufmanneng.stashguard.domain.model.Product

object ProductNotifier {

    private const val CHANNEL_ID = "EXPIRATION_CHANNEL_ID"
    private const val NOTIFICATION_ID = 1

    fun show(context: Context, expiringProducts: List<Product>) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val title = "Ablauf-Warnung"
        val contentText = "Einige Produkte laufen bald ab: ${expiringProducts.first().name}..."
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(expiringProducts.joinToString("\n") { it.name })

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }
}