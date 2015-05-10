package edu.upenn.cis350.rapidresponse;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
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
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by elianamason on 4/9/15.
 * This class allows user to view past notifications that have taken place in their current
 * building and were aimed at the rapid response team in which they're currently enrolled.
 * Also gives user the opportunity to change the building in which they are registered.
 */
public class Homepage extends Activity implements AdapterView.OnItemSelectedListener {

    public final static String EMERG_ID = "edu.upenn.cis350.rapidresponse.MESSAGE";
    String location = null;
    public ParseUser currUser = null;
    public int check = 0;

    /**
     * Method loads UI and view for user, sets up necessary location spinner and
     * Edit/Logout options as well.
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        //connect to Parse
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

        //Record chosen location for user convenience
        TextView loc = (TextView) findViewById(R.id.location_state);
        loc.setText("Current Recorded Building: " +location);

        spinner.setOnItemSelectedListener(this);

        //load 5 most recent emergencies pertaining to user
        displayNotifications();

    }

    /**
     * Class searches for previous emergencies within Parse that took place in registered building
     * and that called the user's current team.
     */
    private void displayNotifications(){

        //Retrieve list of emergencies
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Alert");
        query.whereEqualTo("Roles", currUser.get("Role"));
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

    /**
     * If no emergencies were found in query, populate all buttons with the empty string
     */
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

    /**
     * Given a list of ParseObjects of emergencies pertaining to user, populate the text onto
     * the buttons so that users may see description, room number and have the option to select
     * emergency for further details
     * @param emergencies
     */
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

            if(button != null) {
                button.setText(description);
                String object_id = emergency.getObjectId();
                button.setTag(R.id.unique_id, object_id);
                Log.d("Unique ID Set", object_id);
            }
        }
    }

    /**
     * Item listener for the spinner. Spinner automatically displays user's current location, but
     * changes the location within Parse database for the user once a different location is selected
     * through this spinner
     * @param parent
     * @param view
     * @param pos
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        check = check+1;

        //This is to avoid autochanging when the Activity loads since this method is called upon
        //loading the page
        if (check>1) {
            location = parent.getItemAtPosition(pos).toString();
            if (location.equals("All")) location = "Founders,Pereleman Center,Rhoads,Silverstein";
            TextView loc = (TextView) findViewById(R.id.location_state);
            loc.setText("Current Recorded Building: " + location);
            currUser.put("Building", location);
            currUser.saveInBackground();
            displayNotifications();
        }
    }

    /**
     * When nothing selected on spinner, no action needs to be taken
     * @param parent
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    /**
     * Method run when user selects "Logout" button.
     * Changes view back to Main, prompts user to log back in if desired
     * @param view
     */
    public void onLogoutButtonClick(View view){

        final ProgressDialog dialog = new ProgressDialog(Homepage.this);
        dialog.setMessage("Signing in");
        dialog.show();

        //log out in Parse
        ParseUser.getCurrentUser().put("LoggedIn", false);
        ParseUser.getCurrentUser().saveInBackground();
        ParseUser.logOut();

        //change activity back to Main, option to log in
        Intent i = new Intent(this, Main.class);
        dialog.dismiss();
        startActivityForResult(i, 1);
    }

    /**
     * Method that sends user to EditActivity to Edit information they input upon login
     * @param view
     */
    public void onEditClick(View view){
        Intent i = new Intent(this, EditActivity.class);
        startActivityForResult(i,1);
    }

    /**
     * When user clicks on any of the buttons with emergency descriptions on them, this method runs
     * Creates new description page through Emergency.class
     * @param view
     */
    @TargetApi(11)
    public void onButtonClick(View view){
        Intent intent = new Intent(Homepage.this, Emergency.class);

        String emergency_id =(String) view.getTag(R.id.unique_id);
        intent.putExtra(EMERG_ID,emergency_id);
        startActivity(intent);
    }


}
