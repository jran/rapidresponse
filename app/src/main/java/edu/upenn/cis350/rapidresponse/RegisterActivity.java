package edu.upenn.cis350.rapidresponse;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

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
    private View mProgressView;
    private View mEmailSignUpFormView;
    private View mSignUpFormView;

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
        phoneEditText = (EditText) findViewById(R.id.phone);
        password1EditText = (EditText) findViewById(R.id.password1);
        password2EditText = (EditText) findViewById(R.id.password2);
        Spinner dropdown = (Spinner)findViewById(R.id.team);
        String[] items = new String[]{"team 1", "team 2", "team 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
        dropdown = (Spinner)findViewById(R.id.building);
        items = new String[]{"building 1", "building 2", "building 3"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);
        dropdown = (Spinner)findViewById(R.id.role);
        items = new String[]{"role 1", "role 2", "role 3"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_register_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

        mSignUpFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mEmailSignUpFormView = findViewById(R.id.email_login_form);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String s = parent.getItemAtPosition(pos).toString();
        switch (view.getId()) {
            case R.id.team:
                team = s;
                break;
            case R.id.building:
                building = s;
                break;
            case R.id.role:
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
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (firstname.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_firstname));
        }
        if (lastname.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_lastname));
        }
        if (email.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_email));
        }
        if (!email.contains("@")) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_invalid_email));
        }
        if (phone.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_phone));
        }
        if (password1.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (password1.length() < 5) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_invalid_password));
        }
        if (password1.equals(password2)) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_incorrect_password));
        }
        if (team == null) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_team));
        }
        if (building == null) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_building));
        }
        if (role == null) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_role));
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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}



