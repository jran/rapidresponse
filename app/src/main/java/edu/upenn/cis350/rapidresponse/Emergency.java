package edu.upenn.cis350.rapidresponse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import com.parse.ParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by elianamason on 4/21/15.
 */
public class Emergency extends Activity {
    public String EMERGENCY_ID = null;
    public ParseUser user = null; 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency);

        Intent intent = getIntent();
        EMERGENCY_ID = intent.getStringExtra(Homepage.EMERG_ID);
        

        Parse.initialize(this, "MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "F48WFS83CHeSMqNu4i8ugGrVhO3KozZushvS2PKQNNw");
        
        user = ParseUser.getCurrentUser(); 
        if(user==null) {
            Intent i = new Intent(this, Main.class);
                startActivityForResult(i, 1);
        }
        

        displayInformation();
        checkResponse();

    }

    public void checkResponse(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.getInBackground(EMERGENCY_ID, new GetCallback<ParseObject>() {

            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    String declined = (String) object.get("Declined");
                    if(declined!=null){
                    Log.d("Declined Check", declined);
                        if(declined.contains(user.getObjectId()){
                        Button accept = (Button) findViewById(R.id.accept);
                        accept.setBackgroundColor(14087638);
                        accept.setText("");

                        Button decline = (Button) findViewById(R.id.decline);
                        decline.setBackgroundColor(16771304);
                        decline.setText("DECLINED");
                        return;
                    }}

                    String accepted = (String) object.get("Accepted");
                    if(accepted!=null){
                        Log.d("Accepted check", accepted);
                    if(accepted.contains(user.getObjectId()){
                        Button accept = (Button) findViewById(R.id.accept);
                        accept.setBackgroundColor(14087638);
                        accept.setText("");

                        Button decline = (Button) findViewById(R.id.decline);
                        decline.setBackgroundColor(16771304);
                        decline.setText("DECLINED");
                        return;
                    }}


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
                if (e == null) {
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

                    TextView number = (TextView) findViewById(R.id.phone_num);
                    number.setText(object.getString("Phone"));
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

                    String responded = (String) object.get("Accepted");
                    if(responded==null) responded = "";
                    responded = responded + USER + "\n";
                    object.put("Accepted", responded);

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

                    String declined = (String) object.get("Declined");
                    if(declined==null) declined="";
                    declined = declined + USER + "\n";
                    object.put("Declined", declined);

                    Log.d("Declining", "Did it decline?" + declined);

                } else {
                    Log.d("Tried to decline", e.getMessage());
                }
            }
        });
    }
}



