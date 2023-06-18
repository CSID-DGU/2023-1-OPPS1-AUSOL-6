package com.example.keepfresh;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    public static Boolean initExp = false;
    public static Boolean addCart = false;
    public static String name;

    public AlarmManager alarmManager;
    public SharedPreferences prefs;
    boolean alarm_enable;

    Intent receiverIntent;
    PendingIntent pendingIntent;

    public MyApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration userConfig = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(userConfig);

        receiverIntent = new Intent(this, AlertReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        alarm_enable = prefs.getBoolean("alert_enable", true);

        setAlarm();
    }

    public void cancelAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void setAlarm() {
        // Check if push is available based on the option value
        String alert_t = prefs.getString("alert_time", "오전 9시");
        int alertTime = parseTime(alert_t);
        if (!alarm_enable) {
            return;
        }
        // Cancel previous push before registering the new one
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        // Set the alarm to start at the specified hour and minute
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, alertTime);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        // If the specified time has already passed, set the alarm for the next day
        if (cal.getTime().before(new Date())) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }

    public int parseTime(String alert_t) {
        switch (alert_t){
            case "오전 12시":
                return 0;
            case "오전 1시":
                return 1;
            case "오전 2시":
                return 2;
            case "오전 3시":
                return 3;
            case "오전 4시":
                return 4;
            case "오전 5시":
                return 5;
            case "오전 6시":
                return 6;
            case "오전 7시":
                return 7;
            case "오전 8시":
                return 8;
            case "오전 9시":
                return 9;
            case "오전 10시":
                return 10;
            case "오전 11시":
                return 11;
            case "오후 12시":
                return 12;
            case "오후 1시":
                return 13;
            case "오후 2시":
                return 14;
            case "오후 3시":
                return 15;
            case "오후 4시":
                return 16;
            case "오후 5시":
                return 17;
            case "오후 6시":
                return 18;
            case "오후 7시":
                return 19;
            case "오후 8시":
                return 20;
            case "오후 9시":
                return 21;
            case "오후 10시":
                return 22;
            case "오후 11시":
                return 23;
            default:
                return 9;

        }
    }
}