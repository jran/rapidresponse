package edu.upenn.cis350.rapidresponse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EditActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText password1EditText;
    private EditText password2EditText;
    private EditText oldpasswordEditText;
    private String team;
    private String building;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Set up the login form.
        team = null;
        building = null;
        role = null;
        firstNameEditText = (EditText) findViewById(R.id.editFirstName);
        lastNameEditText = (EditText) findViewById(R.id.editLastName);
        emailEditText = (EditText) findViewById(R.id.editEmail);
        password1EditText = (EditText) findViewById(R.id.editPassword1);
        password2EditText = (EditText) findViewById(R.id.editPassword2);
        oldpasswordEditText = (EditText) findViewById(R.id.oldPassword);
        phoneEditText = (EditText) findViewById(R.id.editPhone);
        phoneEditText.addTextChangedListener(new TextWatcher() {
            private boolean flag = true;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(flag) {
                    String number = s.toString();
                    int i = 0;
                    while (i < number.length()) {
                        if (number.charAt(i) == '-' && i != number.length()) {
                            number = number.substring(0, i) + number.substring(i+1, number.length());
                        } else if (number.charAt(i) == '-') {
                            number = number.substring(0, i);
                        }
                        i++;
                    }
                    if(number.length() != 10){
                        flag = false;
                        phoneEditText.setText(number);
                    } else if (number.length() == 10) {
                        number = number.substring(0, 3) + "-" + number.substring(3, 6) + "-" + number.substring(6);
                        flag = false;
                        phoneEditText.setText(number);
                    }
                } else {
                    flag = true;
                }
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();

        String compareValue= currentUser.getString("Team");
        Spinner teamSpinner = (Spinner)findViewById(R.id.editTeamSpinner);
        String[] items = new String[]{"Medical Rapid Response", "Surgical Rapid Response", "OB Emergency", "Anesthesia Stat", "Code Call", "Airway Emergency"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        teamSpinner.setAdapter(adapter);
        if (!compareValue.equals(null)) {
            int spinnerPosition = adapter.getPosition(compareValue);
            teamSpinner.setSelection(spinnerPosition);
            spinnerPosition = 0;
        }
        teamSpinner.setOnItemSelectedListener(this);

        compareValue= currentUser.getString("Building");
        Spinner buildingSpinner = (Spinner)findViewById(R.id.editBuildingSpinner);
        items = new String[]{"Founders", "Pereleman Center", "Rhoads", "Silverstein"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        buildingSpinner.setAdapter(adapter);
        if (!compareValue.equals(null)) {
            int spinnerPosition = adapter.getPosition(compareValue);
            teamSpinner.setSelection(spinnerPosition);
            spinnerPosition = 0;
        }
        buildingSpinner.setOnItemSelectedListener(this);

        compareValue= currentUser.getString("Role");
        Spinner roleSpinner = (Spinner)findViewById(R.id.editRoleSpinner);
        items = new String[]{"Medicine Resident", "Surgical Resident", "OB Resident", "Pharmacist",
                "CCOPS", "Respiratory Therapy", "Coordinator", "Anesthesia", "Other"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        roleSpinner.setAdapter(adapter);
        if (!compareValue.equals(null)) {
            int spinnerPosition = adapter.getPosition(compareValue);
            teamSpinner.setSelection(spinnerPosition);
            spinnerPosition = 0;
        }
        roleSpinner.setOnItemSelectedListener(this);

        //populate the hints
        firstNameEditText.setHint(currentUser.getString("FirstName"));
        lastNameEditText.setHint(currentUser.getString("LastName"));
        phoneEditText.setHint(currentUser.getString("Phone"));
        emailEditText.setHint(currentUser.getEmail());


    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String s = parent.getItemAtPosition(pos).toString();
        switch (parent.getId()) {
            case R.id.editTeamSpinner:
                team = s;
                break;
            case R.id.editBuildingSpinner:
                building = s;
                break;
            case R.id.editRoleSpinner:
                role = s;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }

    public void onSaveButtonClick(View view){
        attemptSave();
    }

    public void onReturnButtonClick(View view){
        Intent i = new Intent(this, Homepage.class);
        startActivityForResult(i, 1);
    }

    public void onDeleteButtonClick(View view){
        ParseUser user = ParseUser.getCurrentUser();
        final Context s = this;
        final ProgressDialog dialog = new ProgressDialog(EditActivity.this);
        dialog.setMessage(getString(R.string.progress_delete));
        dialog.show();
        ParseUser.logInInBackground(user.getUsername(), oldpasswordEditText.getText().toString().trim(), new LogInCallback() {
            public void done(ParseUser currentuser, com.parse.ParseException e) {
                dialog.dismiss();
                if (currentuser != null) {
                    currentuser.deleteInBackground();
                    Intent i = new Intent(s, Main.class);
                    startActivityForResult(i, 1);
                }
                if (e == null) {
                    return;
                }
                if (e.getMessage().contains("invalid login parameters")) {
                    Toast.makeText(s, "Wrong password. Please try again!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void attemptSave() {
        final String firstname = firstNameEditText.getText().toString().trim();
        final String lastname = lastNameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String phone = phoneEditText.getText().toString().trim();
        final String password1 = password1EditText.getText().toString().trim();
        final String password2 = password2EditText.getText().toString().trim();
        Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
        Matcher matcher = pattern.matcher(phone);
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro)+"\n");
        if (!email.contains("@") && email.length() != 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_invalid_email)+"\n");
        }
        if (!matcher.matches() && phone.length() != 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_invalid_phone)+"\n");
        }
        if (password1.length() < 5 && password1.length() != 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_invalid_password)+"\n");
        }
        if (!password1.equals(password2)) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_incorrect_password)+"\n");
        }

        if (validationError) {
            Toast.makeText(EditActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(EditActivity.this);
        dialog.setMessage(getString(R.string.progress_update));
        dialog.show();

        final ParseUser user = ParseUser.getCurrentUser();
        final Context s = this;

        ParseUser.logInInBackground(user.getEmail(), oldpasswordEditText.getText().toString().trim(), new LogInCallback() {
            public void done(ParseUser user, com.parse.ParseException e) {
                if (user != null) {
                    // Update Parse user
                    if (firstname.length() != 0) {
                        user.put("FirstName", firstname);
                    }
                    if (lastname.length() != 0) {
                        user.put("LastName", lastname);
                    }
                    if (email.length() != 0) {
                        user.setEmail(email);
                        user.setUsername(email);
                    }
                    if (phone.length() != 0) {
                        user.put("Phone", phone);
                    }
                    if (password1.length() != 0) {
                        user.setPassword(password1);
                    }
                    if (team != null) {
                        ParsePush.subscribeInBackground(user.getString("Team"));
                        user.put("Team", team);
                        ParsePush.subscribeInBackground(team);
                    }
                    if (building != null) {
                        ParsePush.subscribeInBackground(user.getString("Building"));
                        user.put("Building", building);
                        ParsePush.subscribeInBackground(building);
                    }
                    if (role != null) {
                        ParsePush.subscribeInBackground(user.getString("Role"));
                        user.put("Role", role);
                        ParsePush.subscribeInBackground(role);
                    }

                    // Call the Parse save method
                    user.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            dialog.dismiss();
                            if (e != null) {
                                Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                // Start an intent for the dispatch activity
                                Intent intent = new Intent(EditActivity.this, Homepage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }
                if (e == null) {
                    dialog.dismiss();
                    return;
                }
                if (e.getMessage().contains("invalid login parameters")) {
                    dialog.dismiss();
                    Toast.makeText(s, "Wrong password. Please try again!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
