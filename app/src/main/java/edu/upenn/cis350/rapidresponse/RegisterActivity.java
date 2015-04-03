package edu.upenn.cis350.rapidresponse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity implements AdapterView.OnItemSelectedListener{
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText password1EditText;
    private EditText password2EditText;
    private String team;
    private String building;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Parse.initialize(this, "MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "F48WFS83CHeSMqNu4i8ugGrVhO3KozZvS2PKQNNw");


        // Set up the login form.
        team = null;
        building = null;
        role = null;
        firstNameEditText = (EditText) findViewById(R.id.firstName);
        lastNameEditText = (EditText) findViewById(R.id.lastName);
        emailEditText = (EditText) findViewById(R.id.email);
        password1EditText = (EditText) findViewById(R.id.password1);
        password2EditText = (EditText) findViewById(R.id.password2);
        phoneEditText = (EditText) findViewById(R.id.phone);
        phoneEditText.addTextChangedListener(new TextWatcher() {
            private boolean flag = true;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println(flag);
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
        Spinner teamSpinner = (Spinner)findViewById(R.id.teamSpinner);
        String[] items = new String[]{"team 1", "team 2", "team 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        teamSpinner.setAdapter(adapter);
        teamSpinner.setOnItemSelectedListener(this);
        Spinner buildingSpinner = (Spinner)findViewById(R.id.buildingSpinner);
        items = new String[]{"building 1", "building 2", "building 3"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        buildingSpinner.setAdapter(adapter);
        buildingSpinner.setOnItemSelectedListener(this);
        Spinner roleSpinner = (Spinner)findViewById(R.id.roleSpinner);
        items = new String[]{"role 1", "role 2", "role 3"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        roleSpinner.setAdapter(adapter);
        roleSpinner.setOnItemSelectedListener(this);

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_register_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String s = parent.getItemAtPosition(pos).toString();
        switch (parent.getId()) {
            case R.id.teamSpinner:
                team = s;
                break;
            case R.id.buildingSpinner:
                building = s;
                break;
            case R.id.roleSpinner:
                role = s;
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptSignUp() {
        String firstname = firstNameEditText.getText().toString().trim();
        String lastname = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password1 = password1EditText.getText().toString().trim();
        String password2 = password2EditText.getText().toString().trim();
        Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
        Matcher matcher = pattern.matcher(phone);
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro)+"\n");
        if (firstname.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_firstname)+"\n");
        }
        if (lastname.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_lastname)+"\n");
        }
        if (email.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_email)+"\n");
        }
        if (!email.contains("@")) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_invalid_email)+"\n");
        }
        if (phone.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_phone)+"\n");
        }
        if (!matcher.matches()) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_invalid_phone)+"\n");
        }
        if (password1.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password)+"\n");
        }
        if (password1.length() < 5) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_invalid_password)+"\n");
        }
        if (!password1.equals(password2)) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_incorrect_password)+"\n");
        }
        if (team == null) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_team)+"\n");
        }
        if (building == null) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_building)+"\n");
        }
        if (role == null) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_role)+"\n");
        }

        if (validationError) {
            Toast.makeText(RegisterActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        // Set up a new Parse user
        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password1);
        user.setEmail(email);
        user.put("Phone", phone);
        user.put("Team", team);
        user.put("Building", building);
        user.put("Role", role);


        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}



