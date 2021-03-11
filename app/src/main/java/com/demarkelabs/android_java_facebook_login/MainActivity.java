package com.demarkelabs.android_java_facebook_login;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button logout_button = findViewById(R.id.logout_button);
        logout_button.setOnClickListener(v -> {
            final ProgressDialog dlg = new ProgressDialog(MainActivity.this);
            dlg.setTitle("Please, wait a moment.");
            dlg.setMessage("Logging out...");
            dlg.show();
            LoginManager.getInstance().logOut();
            ParseUser.logOutInBackground(e -> {
                if (e == null)
                    showAlert("So, you're going...", "Ok...Bye-bye then", true);
                else
                    showAlert("Error...", e.getMessage(), false);
            });
        });
    }


    private void showAlert(String title, String message, boolean isOk) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    if (isOk) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}