package org.mobileappdev.celebri;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
    private ArrayList<String> name, age, bday, favoriteItems;
    private ArrayList<Integer> friendIds;

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
        friendIds = new ArrayList<>();

        birthdayRecordAdapter = new BirthdayRecordAdapter(
                this,
                this,
                name,
                age,
                bday,
                favoriteItems,
                db,
                friendIds
        );
        recyclerView.setAdapter(birthdayRecordAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        add.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
            startActivity(intent);
            finish();
        });

        displayFriendsInfo();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF8383")));
        }
    }

    public void displayFriendsInfo() {
        name.clear();
        age.clear();
        bday.clear();
        favoriteItems.clear();

        Cursor cursor = db.getAllFriendInformation(pref.getUserId());

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Add a friend to your list", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                int friendId = cursor.getInt(cursor.getColumnIndexOrThrow("friend_id"));
                String friendName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String friendBday = cursor.getString(cursor.getColumnIndexOrThrow("birthday"));
                String favoriteItem = cursor.getString(cursor.getColumnIndexOrThrow("item"));

                int turningAge = getTurningAge(friendBday);

                friendIds.add(friendId);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.about_id) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.developers_id) {
            Intent intent = new Intent(this, DevelopersActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.log_out_id) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        pref.clearUserData();
                        Intent intent = new Intent(this, SigninActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }
        return super.onOptionsItemSelected(item);
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
