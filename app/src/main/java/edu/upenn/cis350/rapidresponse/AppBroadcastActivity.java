package edu.upenn.cis350.rapidresponse;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONObject;

import java.lang.Exception;import java.lang.Override;import java.lang.String;import java.text.ParseException;
import java.util.Iterator;

import javax.security.auth.callback.Callback;import edu.upenn.cis350.rapidresponse.Homepage;import edu.upenn.cis350.rapidresponse.R;


public class AppBroadcastActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_broadcast);
        ParseAnalytics.trackAppOpened(getIntent());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        Intent intent = getIntent();

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Message"));

            Iterator itr = json.keys();
            while (itr.hasNext()) {
                String key = (String) itr.next();
            }
            String message = json.getString("alert");
        } catch (Exception e) {

        }

        Button ResponseButton = (Button) findViewById(R.id.response_button);
        ResponseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptResponse();
            }
        });
    }

    public void attemptResponse() {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("OnTheWay", true);
        user.saveInBackground(new SaveCallback() {
            public void done(com.parse.ParseException e) {
                if (e != null) {
                    // Show the error message
                    Toast.makeText(AppBroadcastActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(AppBroadcastActivity.this, Homepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_app_broadcast, container, false);
            return rootView;
        }
    }
}
