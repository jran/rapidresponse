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
 * Class creates and displays a class that gives details on a specific emergency, allows user to
 * Accept or Reject notification (and contains a record of their response) and gives user option
 * to call number specified for more information on the emergency
 */
public class Emergency extends Activity {
    public String EMERGENCY_ID = null;
    public ParseUser currUser = null;
    public String phone_num;
    public boolean responded = false;

    /**
     * Creates UI that displays details of selected emergency
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency);

        Intent intent = getIntent();
        EMERGENCY_ID = intent.getStringExtra(Homepage.EMERG_ID);
        
        //connect to Parse to be able to query emergency information
        Parse.initialize(this, "MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "F48WFS83CHeSMqNu4i8ugGrVhO3KozZvS2PKQNNw");

        currUser = ParseUser.getCurrentUser();
        if(currUser==null){
            Intent i = new Intent(Emergency.this, Homepage.class);
            startActivity(i);
        }
        displayInformation();
        checkResponse();

    }

    /**
     * Checks to see if user has responded. If user has responded, instead of displaying the
     * buttons (Accept vs. Reject), user sees a record of response instead.
     */
    public void checkResponse(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.getInBackground(EMERGENCY_ID, new GetCallback<ParseObject>() {

            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    //check to see if user has accepted
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
                            responded = true;
                            return;
                        }
                    }
                    //check to see if user has declined
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
                            responded = true;
                        }
                    }



                } else {
                    Log.d("Check if responded", e.getMessage());
                }
            }
        });
    }

    /**
     * Displays information specific to emergency (Description, location, Time emergency was
     * created, etc. Also allows user to call a specified number for more information and choose to
     * accept or reject emergency
     */
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

    /**
     * Button allows user to navigate back to Homepage with overall listing of most recent
     * applicable emergencies
     * @param view
     */
    public void onReturnClick(View view) {
        Intent intent = new Intent(Emergency.this, Homepage.class);
        startActivity(intent);
    }

    /**
     * User accepts emergency, this response is recorded into Parse by inserting user's
     * identification number (objectid) into the "Accepted" column
     * @param view
     */
    public void onAcceptButtonClick(View view) {
        //if they've already responded, don't allow them to respond again or change answer
        if(responded) return;

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

                } else {
                    Log.d("Tried to accept", e.getMessage());
                }
            }
        });
    }

    public void onDeclineButtonClick(View view) {
        //if they're already responded, don't allow to respond again or change answer
        if(responded) return;

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

                } else {
                    Log.d("Tried to decline", e.getMessage());
                }
            }
        });
    }

    /**
     * Allows user to call phone number specified directly. Will shift into user's phone interface
     * and will make a call. Once call has finished, will return to this screen in the app.
     * @param view
     */
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



