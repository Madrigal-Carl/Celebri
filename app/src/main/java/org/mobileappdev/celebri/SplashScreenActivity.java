package org.mobileappdev.celebri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        pref = new SharedPreferences(this);

        Thread mythread = new Thread(() -> {
            try {
                Thread.sleep(3000);

                if (pref.isLoggedIn()) {
                    Intent myIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(myIntent);
                    finish();
                } else {
                    Intent myIntent = new Intent(SplashScreenActivity.this, SigninActivity.class);
                    startActivity(myIntent);
                    finish();
                }
            } catch (Exception e) {
            }
        });

        mythread.start();
    }
}