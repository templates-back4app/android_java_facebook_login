package com.demarkelabs.android_java_facebook_login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.facebook.ParseFacebookUtils;

import java.util.Arrays;
import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button logout_button = findViewById(R.id.logout_button);
        final Button link_button = findViewById(R.id.link_button);
        final Button unlink_button = findViewById(R.id.unlink_button);
        final TextView textView = findViewById(R.id.textView);

        textView.setText(getString(R.string.welcome) + "\n");
        textView.setText("Welcome to My App\n" + ParseUser.getCurrentUser().getUsername() + "\n\n\nEmail: " + ParseUser.getCurrentUser().getEmail());


        logout_button.setOnClickListener(v -> {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Please, wait a moment.");
            dialog.setMessage("Logging out...");
            dialog.show();
            LoginManager.getInstance().logOut();
            ParseUser.logOutInBackground(e -> {
                if (e == null)
                    showAlert("So, you're going...", "Ok...Bye-bye then", true);
                else
                    showAlert("Error...", e.getMessage(), false);
            });
        });

        link_button.setOnClickListener(v -> {
            Collection<String> permissions = Arrays.asList("public_profile", "email");
            if (!ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                ParseFacebookUtils.linkWithReadPermissionsInBackground(ParseUser.getCurrentUser(), this, permissions, ex -> {
                    if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                        Toast.makeText(this, "Woohoo, user logged in with Facebook.", Toast.LENGTH_LONG).show();
                        Log.d("FacebookLoginExample", "Woohoo, user logged in with Facebook!");
                    }
                });
            } else {
                Toast.makeText(this, "You have already linked your account with Facebook.", Toast.LENGTH_LONG).show();
            }
        });

        unlink_button.setOnClickListener(v -> {
            ParseFacebookUtils.unlinkInBackground(ParseUser.getCurrentUser(), ex -> {
                if (ex == null) {
                    Toast.makeText(this, "The user is no longer associated with their Facebook account.", Toast.LENGTH_LONG).show();
                    Log.d("MyApp", "The user is no longer associated with their Facebook account.");
                } else {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void showAlert(String title, String message, boolean isOk) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    if (isOk) {
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}