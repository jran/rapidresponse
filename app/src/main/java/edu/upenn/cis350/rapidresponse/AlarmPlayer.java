package edu.upenn.cis350.rapidresponse;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by jran on 2015/4/30.
 */
public class AlarmPlayer {
    private static MediaPlayer mPlayer;
    private static boolean on=false;


    public static void initialize(Context c){
        mPlayer = MediaPlayer.create(c, R.raw.loudalert);
    }

    public  static void play(){
        on = true;
        mPlayer.start();
        mPlayer.setLooping(true);
    }

    public static void stop(){
        on = false;
        mPlayer.stop();
    }

    public static boolean isOn(){
        return on;
    }


}
