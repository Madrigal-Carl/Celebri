package org.mobileappdev.celebri;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    EditText email, password, confirm_password;
    Button reset, back;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        reset = findViewById(R.id.reset_passwordBtn);
        back = findViewById(R.id.returnBtn);

        back.setOnClickListener(this);
        reset.setOnClickListener(this);

        db = new DatabaseHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_passwordBtn:
                if (!validation()){
                    return;
                }

                if (checkEmail()){
                    resetPassword();
                } else {
                    Toast.makeText(this, "No username found.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.returnBtn:
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

    private boolean validation() {
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
            return false;
        }

        if (!pass.equals(c_pass)) {
            confirm_password.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private boolean checkEmail() {
        String user = email.getText().toString().trim();

        if (db.checkEmail(user)){
            return true;
        }
        return false;
    }

    private void resetPassword() {
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Are you sure you want to reset the password?")
                .setPositiveButton("Continue", (dialog, which) -> {

                    if (db.resetPassword(user, pass)){
                        Toast.makeText(this, "Password has been reset", Toast.LENGTH_SHORT).show();
                    }

                    Intent signinIntent = new Intent(this, SigninActivity.class);
                    startActivity(signinIntent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Dismiss the dialog
                    dialog.dismiss();
                })
                .create()
                .show();
    }
}