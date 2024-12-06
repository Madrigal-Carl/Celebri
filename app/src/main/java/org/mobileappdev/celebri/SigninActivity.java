package org.mobileappdev.celebri;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener{

    EditText email, password;
    TextView signup, forgot_password;
    Button login;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.loginBtn);
        forgot_password = findViewById(R.id.forgot_password);

        signup.setOnClickListener(this);
        login.setOnClickListener(this);
        forgot_password.setOnClickListener(this);

        db = new DatabaseHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup:
                Intent signupIntent = new Intent(this, SignupActivity.class);
                startActivity(signupIntent);
                finish();
                break;

            case R.id.forgot_password:
                Intent forgotPassIntent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(forgotPassIntent);
                finish();
                break;

            case R.id.loginBtn:
                userAuth();
                break;
        }
    }

    protected void exitByBackKey() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit the application?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onBackPressed() {
        exitByBackKey();
        super.onBackPressed();
    }

    private void userAuth() {
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (user.isEmpty() && pass.isEmpty()) {
            email.setError("Input your username");
            password.setError("Input your password");
            return;
        } else if (user.isEmpty()) {
            email.setError("Input your username");
            return;
        } else if (pass.isEmpty()) {
            password.setError("Input your password");
            return;
        }

        if (db.userAuth(user, pass)) {
            SharedPreferences pref = new SharedPreferences(this);

            Intent toMain = new Intent(this, MainActivity.class);
            startActivity(toMain);
            finish();
        } else {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }
}