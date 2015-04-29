package edu.upenn.cis350.rapidresponse;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by elianamason on 4/9/15.
 */
public class Homepage extends Activity implements AdapterView.OnItemSelectedListener {
    public final static String EMERG_ID = "edu.upenn.cis350.rapidresponse.MESSAGE";
    String location = null;
    public ParseUser currUser = null;
    public int check = 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Intent intent = getIntent();

        Parse.initialize(this, "MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "F48WFS83CHeSMqNu4i8ugGrVhO3KozZvS2PKQNNw");
        currUser = ParseUser.getCurrentUser();

        Spinner spinner = (Spinner) findViewById(R.id.location_info);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        location = currUser.getString("Building");
        TextView loc = (TextView) findViewById(R.id.location_state);
        loc.setText("Current Recorded Building: " +location);

        spinner.setOnItemSelectedListener(this);

        displayNotifications();

    }

    private void displayNotifications(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.whereEqualTo("EmergencyType",currUser.get("Team"));
        query.whereContains("Building", currUser.get("Building").toString());
        query.orderByDescending("createdAt");
        query.setLimit(5);
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(List<ParseObject> emergencyList, ParseException e) {
                if (e == null) {
                    Log.d("Building", "Retrieved " + emergencyList.size() + " emergencies");
                    if(emergencyList.size()==0){
                        clearContents();
                        return;
                    }
                    populateButtons(emergencyList);
                } else {
                    Log.d("Building", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void clearContents(){

        Button b = (Button) findViewById(R.id.button1);
        b.setText("");

        b = (Button) findViewById(R.id.button2);
        b.setText("");

        b = (Button) findViewById(R.id.button3);
        b.setText("");

        b = (Button) findViewById(R.id.button4);
        b.setText("");

        b = (Button) findViewById(R.id.button5);
        b.setText("");
    }
    @TargetApi(11)
    private void populateButtons(List<ParseObject> emergencies){
        for(int i = 0; i<emergencies.size(); i++){
            ParseObject emergency = emergencies.get(i);
            String description = emergency.getString("Description");


            Button button = null;
            if(i==0) button = (Button)findViewById(R.id.button1);
            else if(i==1) button = (Button)findViewById(R.id.button2);
            else if(i==2) button = (Button)findViewById(R.id.button3);
            else if(i==3) button = (Button)findViewById(R.id.button4);
            else if(i==4) button = (Button)findViewById(R.id.button5);
            else Log.d("Button Identification", "FAILED TO IDENTIFY");

            button.setText(description);
            String object_id = emergency.getObjectId();
            button.setTag(R.id.unique_id, object_id);
            Log.d("Unique ID Set", object_id);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        check = check+1;
        if (check>1){
        location = parent.getItemAtPosition(pos).toString();
        TextView loc = (TextView) findViewById(R.id.location_state);
        loc.setText("Current Recorded Building: " +location);
        currUser.put("Building", location);
        currUser.saveInBackground();
        displayNotifications();}
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void onLogoutButtonClick(View view){
        ParseUser.getCurrentUser().logOut();

        Intent i = new Intent(this, Main.class);
        startActivityForResult(i, 1);
    }
    @TargetApi(11)
    public void onButtonClick(View view){
        Intent intent = new Intent(Homepage.this, Emergency.class);

        String emergency_id =(String) view.getTag(R.id.unique_id);
        intent.putExtra(EMERG_ID,emergency_id);
        startActivity(intent);
    }


}
