package dev.csaba.diygpsmanager.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.csaba.diygpsmanager.R
import dev.csaba.diygpsmanager.ui.MainActivity
import kotlin.random.Random


class MessagingService : FirebaseMessagingService() {
    companion object {
        private const val GEO_FENCE_CHANNEL_ID = "geo_fence_channel"
        private const val CHANNEL_NAME = "Geofence notification"
        private const val CHANNEL_DESCRIPTION = "Tracker to Manager notification"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt(3000)

        /*
         Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
         to at least one of them. Therefore, confirm if version is Oreo or higher, then setup notification channel
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          setupChannels(notificationManager)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val largeIcon = BitmapFactory.decodeResource(
            resources,
            R.drawable.ic_warning
        )
        val notificationSoundUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(this, GEO_FENCE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_warning)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.data["title"])
                .setContentText(remoteMessage.data["message"])
                .setAutoCancel(true)
                .setSound(notificationSoundUri)
                .setContentIntent(pendingIntent)

        // Set notification color to match your app color template
        // Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        notificationBuilder.color = resources.getColor(R.color.colorAccent)
        notificationManager.notify(notificationID, notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannel = NotificationChannel(
            GEO_FENCE_CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
            lightColor = Color.RED
        }
        adminChannel.enableLights(true)
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)
    }
}
