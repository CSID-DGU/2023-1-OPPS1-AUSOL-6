package com.example.keepfresh;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.keepfresh.MainActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class AlertReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "ChannelName";
    SharedPreferences prefs;
    private static Realm realm;


    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        prefs=PreferenceManager.getDefaultSharedPreferences(context);

        createNotificationChannel(context);
        deliverNotification(context);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID, // Channel ID
                    CHANNEL_NAME, // Channel name
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.enableLights(true); // Enable lights
            notificationChannel.setLightColor(R.color.purple_500); // Set light color
            notificationChannel.enableVibration(false); // Enable vibration
            notificationChannel.setDescription(context.getString(R.string.app_name)); // Channel description
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void deliverNotification(Context context) {
        Intent contentIntent = new Intent(context, MainActivity.class);
        String alert_d = prefs.getString("alert_date", "3일 전");
        int alretDate = parseDate(alert_d);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID, // Request code
                contentIntent, // Intent to be launched when the notification is clicked
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Icon
                .setContentTitle(makeNotificationTitle()) // Title
                .setContentText(makeNotificationMessage()) // Content
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
    private String makeNotificationTitle(){

        // debug
        return "test";
    }
    private String makeNotificationMessage(){

        // debug
        return "test";
    }
    private int parseDate(String alert_d) {
        switch(alert_d){
            case "1일 전":
                return 1;
            case "3일 전":
                return 3;
            case "7일 전":
                return 7;
            default:
                return 3;
        }
    }
}
