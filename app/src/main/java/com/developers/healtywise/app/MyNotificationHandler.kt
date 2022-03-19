package com.developers.healtywise.app

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.utils.Constants
import com.developers.healtywise.presentation.activities.MainActivity
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.notifications.handler.NotificationHandler
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

class MyNotificationHandler(private val context: Context) : NotificationHandler {
    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun showNotification(channel: Channel, message: Message) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("channelId",channel.cid)
        intent.action = Constants.ACTION_NEW_MESSAGE_SENT
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationId = Random.nextInt()
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

       val  notification=NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.healty_logo)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    override fun dismissChannelNotifications(channelType: String, channelId: String) {
        // Dismiss all notification related with this channel
    }

    override fun dismissAllNotifications() {
        // Dismiss all notifications
    }
}