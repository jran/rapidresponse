package edu.upenn.cis350.rapidresponse;

import android.content.Context;
import android.content.Intent;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONObject;
import java.lang.Override;import java.util.Iterator;

/**
 * Created by Vijay on 4/16/2015.
 */
public class AppBroadcastReciever extends ParsePushBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AppBroadcastActivity.class);
        i.putExtras(intent.getExtras());
        context.startActivity(i);
    }
}
