package org.mobileappdev.celebri;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddFriendActivity extends AppCompatActivity implements View.OnClickListener{

    EditText name, bday, fav_item;
    Button add, cancel;
    DatabaseHelper db;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        name = findViewById(R.id.name);
        bday = findViewById(R.id.bday);
        fav_item = findViewById(R.id.fav_item);
        add = findViewById(R.id.add_birthdayBtn);
        cancel = findViewById(R.id.cancelBtn);

        add.setOnClickListener(this);
        cancel.setOnClickListener(this);

        db = new DatabaseHelper(this);
        pref = new SharedPreferences(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_birthdayBtn:
                if (!validation()){
                    return;
                }

                addFriend();
                break;

            case R.id.cancelBtn:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void addFriend() {
        int id = pref.getUserId();
        String user = name.getText().toString().trim();
        String date = bday.getText().toString().trim();
        String item = fav_item.getText().toString().trim();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Details");
        builder.setMessage("Please confirm the details:\n\n"
                + "Name: " + user + "\n"
                + "Birthday: " + date + "\n"
                + "Favorite Item: " + (item.isEmpty() ? "None" : item));
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            if (db.addFriend(id, user, date, item)) {
                Toast.makeText(this, "Friend added successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to add friend. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private boolean validation() {
        String user = name.getText().toString().trim();
        String birth = bday.getText().toString().trim();

        if (user.isEmpty() || birth.isEmpty()) {
            if (user.isEmpty()) {
                name.setError("Enter a name");
            }
            if (birth.isEmpty()) {
                bday.setError("Enter a birthdate");
            }
            return false;
        }

        if (user.length() < 8) {
            name.setError("Password must be at least 8 characters");
            return false;
        }

        if (!birth.matches("^(0[1-9]|1[0-2])/([0-2][0-9]|3[0-1])/\\d{4}$")) {
            bday.setError("Enter a valid date in MM/DD/YYYY format");
            return false;
        }

        if (!isValidDate(birth)) {
            bday.setError("Enter a valid date");
            return false;
        }

        return true;
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }


}