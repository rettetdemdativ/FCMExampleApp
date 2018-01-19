package go.vienna.fcmexample.fcmexampleapp.services

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import go.vienna.fcmexample.fcmexampleapp.R
import go.vienna.fcmexample.fcmexampleapp.views.MainActivity


/**
 * FCMExampleApp
 * Author(s): Michael Koeppl
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage?) {
        Log.i(TAG, "Message from %s received: %s".format(message?.from, message?.data))

        if (message?.data != null) {
            sendLocalBroadcast(message.data)
            displayNotification(message.data)
        }
    }

    /**
     * Sends the Broadcast message which the main presenter listens for. Through the Broadcast
     * we tell the receivers that a new message has arrived.
     */
    private fun sendLocalBroadcast(messageData: Map<String, String>) {
        val broadcaster = LocalBroadcastManager.getInstance(baseContext)
        val intent = Intent(FCM_MSG)
        intent.putExtra("message", messageData["message"])
        broadcaster.sendBroadcast(intent)
    }

    /**
     * Defines a notification (with notification channels for Android versions 8.0 and later
     * versions) and displays it.
     */
    private fun displayNotification(messageData: Map<String, String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(getNotificationChannel())
        }

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_brightness_1_black_24dp)
                .setContentTitle("New notification")
                .setContentText(messageData["message"])

        val resultIntent = Intent(this, MainActivity::class.java)

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationBuilder.setContentIntent(resultPendingIntent)

        notificationManager.notify(0, notificationBuilder.build())
    }

    /**
     * Sets up the notification channel required for Android 8.0 and later versions.
     */
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNotificationChannel(): NotificationChannel {
        val id = CHANNEL_ID
        val name = getString(R.string.notification_channel_name)
        val description = getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH

        val notificationChannel = NotificationChannel(id, name, importance)
        notificationChannel.description = description
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        return notificationChannel
    }

    companion object {
        private const val TAG = "MyFBMessagingService"
        private const val CHANNEL_ID = "fcm_example_notifications"

        const val FCM_MSG = "NewFirebaseMessage"
    }
}
