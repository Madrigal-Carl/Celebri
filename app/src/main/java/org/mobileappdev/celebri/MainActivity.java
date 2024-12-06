package org.mobileappdev.celebri;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton add;
    RecyclerView recyclerView;
    private ArrayList<String> name;
    private ArrayList<String> age;
    private ArrayList<String> bday;
    private ArrayList<String> favoriteItems;

    BirthdayRecordAdapter birthdayRecordAdapter;
    DatabaseHelper db;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        add = findViewById(R.id.addBtn);

        db = new DatabaseHelper(this);
        pref = new SharedPreferences(this);

        name = new ArrayList<>();
        age = new ArrayList<>();
        bday = new ArrayList<>();
        favoriteItems = new ArrayList<>();

        birthdayRecordAdapter = new BirthdayRecordAdapter(
                this,
                this,
                name,
                age,
                bday,
                favoriteItems
        );
        recyclerView.setAdapter(birthdayRecordAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        add.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
            startActivity(intent);
        });

        displayFriendsInfo();
    }

    public void displayFriendsInfo() {
        name.clear();
        age.clear();
        bday.clear();
        favoriteItems.clear();

        Cursor cursor = db.getAllFriendInformation(pref.getUserId());

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No friends found", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String friendName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String friendBday = cursor.getString(cursor.getColumnIndexOrThrow("birthday"));
                String favoriteItem = cursor.getString(cursor.getColumnIndexOrThrow("item"));

                int turningAge = getTurningAge(friendBday);

                name.add(friendName);
                age.add(String.valueOf(turningAge));
                bday.add(friendBday);
                favoriteItems.add(favoriteItem);
            }

            birthdayRecordAdapter.notifyDataSetChanged();
        }

        cursor.close();
    }

    private int getTurningAge(String birthday) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date birthDate = dateFormat.parse(birthday);

            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(birthDate);

            Calendar currentCalendar = Calendar.getInstance();
            int currentYear = currentCalendar.get(Calendar.YEAR);

            int age = currentYear - birthCalendar.get(Calendar.YEAR);

            if (currentCalendar.get(Calendar.MONTH) > birthCalendar.get(Calendar.MONTH) ||
                    (currentCalendar.get(Calendar.MONTH) == birthCalendar.get(Calendar.MONTH) &&
                            currentCalendar.get(Calendar.DAY_OF_MONTH) >= birthCalendar.get(Calendar.DAY_OF_MONTH))) {
                return age;
            }

            return age - 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit the application?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();

        super.onBackPressed();
    }
}
