package com.demarkelabs.android_java_facebook_login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

import org.json.JSONException;

import java.util.Arrays;
import java.util.Collection;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(v -> {
            final ProgressDialog dlg = new ProgressDialog(LoginActivity.this);
            dlg.setTitle("Please, wait a moment.");
            dlg.setMessage("Logging in...");
            dlg.show();
            Collection<String> permissions = Arrays.asList("public_profile", "email");
            ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, (user, err) -> {
                if (err != null) {
                    dlg.dismiss();
                    ParseUser.logOut();
                    Log.e("FacebookLoginExample", "done: ", err);
                }
                if (user == null) {
                    dlg.dismiss();
                    ParseUser.logOut();
                    Toast.makeText(LoginActivity.this, "The user cancelled the Facebook login.", Toast.LENGTH_LONG).show();
                    Log.d("FacebookLoginExample", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    dlg.dismiss();
                    Toast.makeText(LoginActivity.this, "User signed up and logged in through Facebook.", Toast.LENGTH_LONG).show();
                    Log.d("FacebookLoginExample", "User signed up and logged in through Facebook!");
                    getUserDetailFromFB();
                } else {
                    dlg.dismiss();
                    Toast.makeText(LoginActivity.this, "User logged in through Facebook.", Toast.LENGTH_LONG).show();
                    Log.d("FacebookLoginExample", "User logged in through Facebook!");
                    showAlert("Oh, you!", "Welcome back!", ParseUser.getCurrentUser());
                }
            });
        });
    }

    private void getUserDetailFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), (object, response) -> {
            ParseUser user = ParseUser.getCurrentUser();
            try {
                user.setUsername(object.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                user.setEmail(object.getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            user.saveInBackground(e -> {
                if (e == null) {
                    showAlert("First Time Login!", "Welcome!", user);
                } else
                    showAlert("Error", e.getMessage(), null);
            });
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void showAlert(String title, String message, ParseUser user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    if (user != null) {
                        intent.putExtra("info",user.getUsername()+"\n\n\n"+ "Email: " + user.getEmail());
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}