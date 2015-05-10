package edu.upenn.cis350.rapidresponse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.ParseInstallation;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;

import static com.parse.ParseUser.requestPasswordResetInBackground;


/**
 * A login screen that offers login via email/password and via Google+ sign in.
 *
 */


public class Main extends Activity {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        //populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            final ProgressDialog dialog = new ProgressDialog(Main.this);
            dialog.setMessage("Signing in");
            dialog.show();
            updateUserInstallation(dialog);

            Intent i = new Intent(this, Homepage.class);
            startActivityForResult(i, 1);

        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        final Context s = this;

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        // To go from one activity to another, create an Intent using the current Activity and the Class to be created
        //check login
        final ProgressDialog dialog = new ProgressDialog(Main.this);
        dialog.setMessage("Signing in");
        dialog.show();
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, com.parse.ParseException e) {
                if (user != null) {
                    updateUserInstallation(dialog);
                    Intent i = new Intent(s, Homepage.class);
                    startActivityForResult(i, 1);
                } else if (e.getMessage().contains("invalid login parameters")) {
                    dialog.dismiss();
                    Toast.makeText(s, "Wrong email or password. Please try again!",
                            Toast.LENGTH_LONG).show();
                } else {
                    dialog.dismiss();
                    Toast.makeText(s, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onSignInButtonClick(View view){
        attemptLogin();
    }

    public void onForgotPasswordButtonClick(View view){
        final ProgressDialog dialog = new ProgressDialog(Main.this);
        dialog.setMessage("Verifying Email");
        dialog.show();
        final Context s = this;
        requestPasswordResetInBackground(mEmailView.getText().toString().trim(), new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                dialog.dismiss();
                if (e == null) {
                    Toast.makeText(s, "password reset e-mail has been sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(s, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onRegisterButtonClick(View view) {

        // To go from one activity to another, create an Intent using the current Activity and the Class to be created
        Intent i = new Intent(this, RegisterActivity.class);
        startActivityForResult(i, 1);
    }

    private void updateUserInstallation(ProgressDialog d){
        final ProgressDialog dialog = d;
        ParseUser.getCurrentUser().put("LoggedIn", true);
        ParseUser.getCurrentUser().saveInBackground();
        ParseInstallation.getCurrentInstallation().saveInBackground((new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.v("save in background", e.getMessage());
                }
            }
        }));
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
        ParseInstallation.getCurrentInstallation().put("user", ParseUser.getCurrentUser());
        ParseInstallation.getCurrentInstallation().saveInBackground((new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.v("save in background", e.getMessage());
                }
            }
        }));
    }
}

