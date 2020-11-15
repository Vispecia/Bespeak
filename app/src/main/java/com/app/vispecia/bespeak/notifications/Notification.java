package com.app.vispecia.bespeak.notifications;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.RequiresApi;

public class Notification extends ContextWrapper {

    private static final String ID = "bespeak_id";
    private static final String NAME = "Bespeak";

    private NotificationManager notificationManager;

    public Notification(Context base)
    {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(ID,NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(notificationChannel);
    }


    public NotificationManager getManager(){
        if(notificationManager == null)
        {
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getNotifications(String title, String body, PendingIntent pIntent, Uri sound, String icon){
        return new android.app.Notification.Builder(getApplicationContext(),ID)
                .setContentIntent(pIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sound)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));

    }

}


