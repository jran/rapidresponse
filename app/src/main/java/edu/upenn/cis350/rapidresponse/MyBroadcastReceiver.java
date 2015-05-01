package edu.upenn.cis350.rapidresponse;
import com.parse.ParsePushBroadcastReceiver;


import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import org.json.JSONObject;
import android.util.Log;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
/**
 * Created by jran on 2015/4/29.
 *
 */
public class MyBroadcastReceiver extends ParsePushBroadcastReceiver {
    private static final int MY_NOTIFICATION_ID=1;
    @Override
    public void onReceive(Context c, Intent i) {
    try{
        AlarmPlayer.initialize(c);
        AlarmPlayer.play();

        String jsonData = i.getExtras().getString("com.parse.Data");
        JSONObject json = new JSONObject(jsonData);

        String message = null;
        if(json.has("alert")) {
            message = json.getString("alert");
        }

        String objectId = null;
        if(json.has("ObjectID")) {
            objectId = json.getString("ObjectID");
        }

        if(message != null) {
            generateNotification(c, message);
        }

        Intent intent = new Intent(c, Emergency.class);
        intent.putExtra(Homepage.EMERG_ID, objectId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
    } catch(Exception e) {
        Log.e("NOTIF ERROR", e.toString());
    }
    }
    private void generateNotification(Context context, String message) {

        Intent intent = new Intent(context, Emergency.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setAutoCancel(true)
                        .setDefaults(new NotificationCompat().DEFAULT_VIBRATE);

        mBuilder.setContentIntent(contentIntent);

        mNotifM.notify(MY_NOTIFICATION_ID, mBuilder.build());
    }

}
