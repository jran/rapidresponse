package edu.upenn.cis350.rapidresponse;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by elianamason on 4/21/15.
 *
 */
public class Emergency extends Activity {
    public String EMERGENCY_ID = null;
    public ParseUser currUser = null;
    public String phone_num;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency);

        Intent intent = getIntent();
        EMERGENCY_ID = intent.getStringExtra(Homepage.EMERG_ID);
        

        Parse.initialize(this, "MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "F48WFS83CHeSMqNu4i8ugGrVhO3KozZvS2PKQNNw");

        currUser = ParseUser.getCurrentUser();
        if(currUser==null){
            Intent i = new Intent(Emergency.this, Homepage.class);
            startActivity(i);
        }
        displayInformation();
        checkResponse();

    }

    public void checkResponse(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.getInBackground(EMERGENCY_ID, new GetCallback<ParseObject>() {

            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    String accepted = (String) object.get("Accepted");
                    if(accepted!=null) {
                        Log.d("Accepted check", accepted);
                        Log.d("User check", currUser.getObjectId());
                        if (accepted.contains(currUser.getObjectId())) {
                            Button accept = (Button) findViewById(R.id.accept);
                            accept.setBackgroundColor(14087638);
                            accept.setText("Accepted");

                            Button decline = (Button) findViewById(R.id.decline);
                            decline.setBackgroundColor(16771304);
                            decline.setText("");
                            return;
                        }
                    }

                    String declined = (String) object.get("Declined");
                    if(declined!=null) {
                        Log.d("Declined Check", declined);
                        if (declined.contains(currUser.getObjectId())) {

                            Button accept = (Button) findViewById(R.id.accept);
                            accept.setBackgroundColor(14087638);
                            accept.setText("");

                            Button decline = (Button) findViewById(R.id.decline);
                            decline.setBackgroundColor(16771304);
                            decline.setText("DECLINED");
                        }
                    }



                } else {
                    Log.d("Tried to decline", e.getMessage());
                }
            }
        });
    }

    public void displayInformation() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.getInBackground(EMERGENCY_ID, new GetCallback<ParseObject>() {

            public void done(ParseObject object, ParseException e) {
                if (e == null || e.getMessage().equals("unauthorized")) {
                    TextView describe = (TextView) findViewById(R.id.details);
                    describe.setText(object.getString("Description"));

                    TextView building = (TextView) findViewById(R.id.building);
                    building.setText(object.getString("Building"));

                    TextView loc = (TextView) findViewById(R.id.location_id);
                    loc.setText(object.getString("Location"));

                    TextView time = (TextView) findViewById(R.id.time);
                    DateFormat df = new SimpleDateFormat("MMM dd, yyyy, HH:mm");
                    Date date = object.getCreatedAt();
                    time.setText(df.format(date));

                    Button number = (Button) findViewById(R.id.btncall);
                    number.setText(object.getString("Phone"));
                    phone_num = object.getString("Phone");
                } else {
                    Log.d("Populating Texts", e.getMessage());
                }
            }
        });


    }

    public void onReturnClick(View view) {
        Intent intent = new Intent(Emergency.this, Homepage.class);
        startActivity(intent);
    }

    public void onAcceptButtonClick(View view) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.getInBackground(EMERGENCY_ID, new GetCallback<ParseObject>() {

            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Button accept = (Button) findViewById(R.id.accept);
                    accept.setBackgroundColor(14087638);
                    accept.setText("ACCEPTED");

                    Button decline = (Button) findViewById(R.id.decline);
                    decline.setBackgroundColor(16771304);
                    decline.setText("");
                    if(AlarmPlayer.isOn()) {
                        AlarmPlayer.stop();
                    }
                    String responded = (String) object.get("Accepted");
                    if(responded==null) responded = "";
                    responded = responded + currUser.getObjectId() + ", " + "\n";
                    object.put("Accepted", responded);
                    object.saveInBackground();

                    Log.d("Accepting", "Did it accept?" + responded);

                } else {
                    Log.d("Tried to accept", e.getMessage());
                }
            }
        });
    }

    public void onDeclineButtonClick(View view) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.getInBackground(EMERGENCY_ID, new GetCallback<ParseObject>() {

            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Button accept = (Button) findViewById(R.id.accept);
                    accept.setBackgroundColor(14087638);
                    accept.setText("");

                    Button decline = (Button) findViewById(R.id.decline);
                    decline.setBackgroundColor(16771304);
                    decline.setText("DECLINED");

                    if(AlarmPlayer.isOn()) {
                        AlarmPlayer.stop();
                    }
                    String declined = (String) object.get("Declined");
                    if(declined==null) declined="";
                    declined = declined + currUser.getObjectId() + ", " + "\n";
                    object.put("Declined", declined);
                    object.saveInBackground();

                    Log.d("Declining", "Did it decline?" + declined);

                } else {
                    Log.d("Tried to decline", e.getMessage());
                }
            }
        });
    }

    public void onCallButtonClick(View view){
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phone_num));
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Log.e("Calling a Phone Number", "Call failed", activityException);
        }
    }
}



