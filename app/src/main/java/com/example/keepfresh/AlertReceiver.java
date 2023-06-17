package com.example.keepfresh;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.keepfresh.MainActivity;

public class AlertReceiver {
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "ChannelName";

    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel(context);
        deliverNotification(context);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID, // Channel ID
                    CHANNEL_NAME, // Channel name
                    NotificationManager.IMPORTANCE_HIGH
                    /*
                    1. IMPORTANCE_HIGH: Plays a sound and appears as a heads-up notification.
                    2. IMPORTANCE_DEFAULT: Plays a sound.
                    3. IMPORTANCE_LOW: No sound.
                    4. IMPORTANCE_MIN: No sound and does not appear in the status bar.
                     */
            );
            notificationChannel.enableLights(true); // Enable lights
            notificationChannel.setLightColor(R.color.purple_500); // Set light color
            notificationChannel.enableVibration(true); // Enable vibration
            notificationChannel.setDescription(context.getString(R.string.app_name)); // Channel description
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void deliverNotification(Context context) {
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID, // Request code
                contentIntent, // Intent to be launched when the notification is clicked
                PendingIntent.FLAG_UPDATE_CURRENT
                /*
                1. FLAG_UPDATE_CURRENT: If the PendingIntent already exists, update the extras of the intent.
                2. FLAG_CANCEL_CURRENT: If the PendingIntent already exists, cancel it and create a new one.
                3. FLAG_NO_CREATE: If the PendingIntent already exists, return null instead of creating a new one.
                4. FLAG_ONE_SHOT: The PendingIntent can only be used once.
                 */
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Icon
                .setContentTitle("알림제목") // Title
                .setContentText("알림내용") // Content
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
