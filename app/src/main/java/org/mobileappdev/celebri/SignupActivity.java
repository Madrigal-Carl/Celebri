package org.mobileappdev.celebri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    EditText email, password, confirm_password;
    Button create_accountBtn;
    TextView login;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        create_accountBtn = findViewById(R.id.create_accountBtn);
        login = findViewById(R.id.login);

        create_accountBtn.setOnClickListener(this);
        login.setOnClickListener(this);

        db = new DatabaseHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_accountBtn:
                addUser();
                break;

            case R.id.login:
                Intent intent = new Intent(this, SigninActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void addUser() {
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String c_pass = confirm_password.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty() || c_pass.isEmpty()) {
            if (user.isEmpty()) {
                email.setError("Input your username");
            }
            if (pass.isEmpty()) {
                password.setError("Input your password");
            }
            if (c_pass.isEmpty()) {
                confirm_password.setError("Confirm your password");
            }
            return;
        }

        if (!pass.equals(c_pass)) {
            confirm_password.setError("Passwords do not match");
            return;
        }

        if (!user.endsWith("@gmail.com")) {
            Toast.makeText(this, "Please enter a valid Gmail address.", Toast.LENGTH_SHORT).show();
            return;
        }else if (user.contains(" ")) {
            email.setError("Username must not contain spaces");
            return;
        }

        if (pass.length() < 8) {
            password.setError("Password must be at least 8 characters");
            return;
        } else if (pass.contains(" ")) {
            password.setError("Password must not contain spaces");
            return;
        }

        if (db.addUser(user, pass)) {
            Toast.makeText(this, "Account has been Created", Toast.LENGTH_SHORT).show();
            Intent signinIntent = new Intent(this, SigninActivity.class);
            startActivity(signinIntent);
            finish();
        } else {
            Toast.makeText(this, "Username is already taken", Toast.LENGTH_SHORT).show();
        }
    }

}