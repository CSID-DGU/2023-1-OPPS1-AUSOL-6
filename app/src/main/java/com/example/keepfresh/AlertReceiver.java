package com.example.keepfresh;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.keepfresh.MainActivity;

import java.time.LocalDate;
import java.util.Date;

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
        int alertDate = parseDate(alert_d);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID, // Request code
                contentIntent, // Intent to be launched when the notification is clicked
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_custom) // Icon
                .setContentTitle("유통기한 알림") // Title
                .setContentText("") // Content
                .setStyle(new NotificationCompat.BigTextStyle().bigText(makeNotificationMessage(alertDate)))
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

    private String makeNotificationMessage(int alertDate) {
        realm = Realm.getDefaultInstance();
        RealmResults<ItemList> results = realm.where(ItemList.class).findAll();
        Date now = new Date();
        StringBuilder sb = new StringBuilder();
        int cnt = 0;

        long diff;
        results = results.sort("expire_date", Sort.ASCENDING);
        for (ItemList data : results) {
            diff = data.getExpireDate().getTime() - now.getTime();
            int dDay = (int) (diff / (24 * 60 * 60 * 1000));
            if (dDay > alertDate) break;

            cnt++;
            switch (data.getStorage()) {
                case 0:
                    sb.append("상온 보관중인 ").append(data.getName()).append("의 유통기한이 ")
                            .append(dDay).append("일 남았습니다.\n");
                    break;
                case 1:
                    sb.append("냉장 보관중인 ").append(data.getName()).append("의 유통기한이 ")
                            .append(dDay).append("일 남았습니다.\n");
                    break;
                case 2:
                    sb.append("냉동 보관중인 ").append(data.getName()).append("의 유통기한이 ")
                            .append(dDay).append("일 남았습니다.\n");
                    break;
                default:
                    sb.append(data.getName()).append(" 보관장소 오류!\n");
                    break;
            }
        }

        realm.close();

        if (cnt == 0) {
            return "오늘 유통기한이 임박한 식품은 없습니다.";
        } else {
            sb.insert(0, "유통기한이 임박한 식품이 " + cnt + "개 있습니다.\n");
            return sb.toString();
        }
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
