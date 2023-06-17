package com.example.keepfresh;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.preference.ListPreference;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class SendAlert extends ContextWrapper{

    public static final String channelID = "channelID";
    public static final String channelNm = "channelNm";

    ListPreference datePreference;
    ListPreference timePreference;

    private NotificationManager notiManager;

    public SendAlert(Context base){
        super(base);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)

    public void createChannels(){
        NotificationChannel channel1= new NotificationChannel(channelID, channelNm, NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);
        channel1.enableVibration(false);
        channel1.setLightColor(com.google.android.material.R.color.design_default_color_on_primary);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager();
    }

    public NotificationManager getManager(){
        if(notiManager == null){
            notiManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notiManager;
    }


}
