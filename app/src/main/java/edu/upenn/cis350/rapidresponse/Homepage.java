package edu.upenn.cis350.rapidresponse;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by elianamason on 4/9/15.
 */
public class Homepage extends Activity implements AdapterView.OnItemSelectedListener {
    public static String USER = null;
    public final static String EMERG_ID = "edu.upenn.cis350.rapidresponse.MESSAGE";
    String location = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Intent intent = getIntent();
        USER = intent.getStringExtra(Main.USER);

        Parse.initialize(this, "MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "F48WFS83CHeSMqNu4i8ugGrVhO3KozZvS2PKQNNw");

        Spinner spinner = (Spinner) findViewById(R.id.location_info);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        location = spinner.getItemAtPosition(0).toString();

        spinner.setOnItemSelectedListener(this);

        displayNotifications();

    }

    private void displayNotifications(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        if(!location.equals("All")){
            query.whereEqualTo("Building",location);
            Log.d("Building", "searching within the following location: " + location);
        }
        query.orderByDescending("updatedAt");
        query.setLimit(5);
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(List<ParseObject> emergencyList, ParseException e) {
                if (e == null) {
                    Log.d("Building", "Retrieved " + emergencyList.size() + " emergencies");
                    populateButtons(emergencyList);
                } else {
                    Log.d("Building", "Error: " + e.getMessage());
                }
            }
        });
    }

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
        location = parent.getItemAtPosition(pos).toString();
        TextView loc = (TextView) findViewById(R.id.location_state);
        loc.setText("Current Recorded Building: " +location);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void onLogoutButtonClick(View view){
        Intent i = new Intent(this, Main.class);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.getInBackground(USER, new GetCallback<ParseObject>() {

            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.put("LoggedIn", false);
                } else {
                    Log.d("Logging Out", e.getMessage());
                }
            }
        });


        startActivityForResult(i, 1);
    }

    public void onButtonClick(View view){
        Intent intent = new Intent(Homepage.this, Emergency.class);

        String emergency_id =(String) view.getTag(R.id.unique_id);
        //String message = "EMERG_ID";
        intent.putExtra(EMERG_ID,emergency_id);
        intent.putExtra(USER,USER);
        startActivity(intent);
    }


}
