package edu.upenn.cis350.rapidresponse;
import com.parse.ParsePushBroadcastReceiver;


import android.content.Context;
import android.content.Intent;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
/**
 * Created by jran on 2015/4/29.
 */
public class MyBroadcastReceiver extends ParsePushBroadcastReceiver {
    private static final String TAG = "PushNotificationReceiver";
    private static final int MY_NOTIFICATION_ID=1;
    NotificationManager notificationManager;
    Notification myNotification;
    @Override
    public void onReceive(Context c, Intent i) {
        PendingIntent pi = PendingIntent.getBroadcast(c, 0, new Intent("com.example.andtip"),0 );
        myNotification=new NotificationCompat.Builder(c)
                .setContentTitle("This is a notification that uses a custom sound.")
                .setContentText("Notification")
                .setTicker("Notification!")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .setSound(Uri.parse("android.resource://edu.upenn.cis350.rapidresponse"+R.raw.loudalert))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .build();

        notificationManager = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);

        Intent intent = new Intent(c, Emergency.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
    }
}
