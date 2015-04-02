package edu.upenn.cis350.rapidresponse;


import com.parse.Parse;

/**
 * Created by jran on 2015/4/2.
 */

public class Application extends android.app.Application {

    public void onCreate() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "F48WFS83CHeSMqNu4i8ugGrVhO3KozZvS2PKQNNw");
    }

}