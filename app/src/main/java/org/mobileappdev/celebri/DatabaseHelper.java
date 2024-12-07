package org.mobileappdev.celebri;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DATABASE_NAME = "Celebri.db";
    private static final int DATABASE_VERSION = 1;

    // Users Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";

    // Friends Table
    private static final String TABLE_FRIENDS = "friends";
    private static final String COLUMN_FRIEND_ID = "friend_id";
    private static final String COLUMN_FRIEND_USER_ID = "user_id";
    private static final String COLUMN_FRIEND_NAME = "name";
    private static final String COLUMN_BIRTHDAY = "birthday";

    // Favorite Table
    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_FAVORITE_ID = "favorite_id";
    private static final String COLUMN_FAVORITE_FRIEND_ID = "friend_id";
    private static final String COLUMN_ITEM = "item";
    private static final String COLUMN_ADDED_DATE = "added_date";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Users table
        String userTableQuery = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s VARCHAR(30) NOT NULL UNIQUE, " +
                        "%s VARCHAR(30) NOT NULL)",
                TABLE_USERS, COLUMN_USER_ID, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD
        );
        db.execSQL(userTableQuery);

        // Create the Friends table
        String friendsTableQuery = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s INTEGER NOT NULL, " +
                        "%s VARCHAR(20) NOT NULL, " +
                        "%s DATE NOT NULL, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE)",
                TABLE_FRIENDS, COLUMN_FRIEND_ID, COLUMN_FRIEND_USER_ID, COLUMN_FRIEND_NAME, COLUMN_BIRTHDAY,
                COLUMN_FRIEND_USER_ID, TABLE_USERS, COLUMN_USER_ID
        );
        db.execSQL(friendsTableQuery);

        // Create the Favorites table
        String favoritesTableQuery = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s INTEGER NOT NULL, " +
                        "%s VARCHAR(100) NOT NULL, " +
                        "%s DATE DEFAULT (CURRENT_DATE), " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE)",
                TABLE_FAVORITES, COLUMN_FAVORITE_ID, COLUMN_FAVORITE_FRIEND_ID, COLUMN_ITEM, COLUMN_ADDED_DATE,
                COLUMN_FAVORITE_FRIEND_ID, TABLE_FRIENDS, COLUMN_FRIEND_ID
        );
        db.execSQL(favoritesTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean userAuth(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = String.format(
                "SELECT %s, %s FROM %s WHERE %s = ? AND %s = ?",
                COLUMN_USER_ID, COLUMN_USER_EMAIL, TABLE_USERS, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD
        );

        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            String retrievedEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL));

            SharedPreferences sharedPreferences = new SharedPreferences(context);
            sharedPreferences.setUserId(userId);
            sharedPreferences.setIsLoggedIn(true);

            cursor.close();
            db.close();
            return true;
        }

        cursor.close();
        db.close();
        return false;
    }

    public boolean addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_EMAIL, email);
            values.put(COLUMN_USER_PASSWORD, password);

            long result = db.insert(TABLE_USERS, null, values);

            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                COLUMN_USER_ID, TABLE_USERS, COLUMN_USER_EMAIL
        );

        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean userExists = cursor.moveToFirst();

        cursor.close();
        db.close();

        return userExists;
    }

    public boolean resetPassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_PASSWORD, newPassword);

            int rowsUpdated = db.update(
                    TABLE_USERS,
                    values,
                    COLUMN_USER_EMAIL + " = ?",
                    new String[]{email}
            );

            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean addFriend(int userId, String name, String birthday, String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        long friendId = -1;

        try {
            ContentValues friendValues = new ContentValues();
            friendValues.put(COLUMN_FRIEND_USER_ID, userId);
            friendValues.put(COLUMN_FRIEND_NAME, name);
            friendValues.put(COLUMN_BIRTHDAY, birthday);

            friendId = db.insert(TABLE_FRIENDS, null, friendValues);

            if (friendId == -1) {
                return false;
            }

            if (item == null || item.trim().isEmpty()) {
                item = "None";
            }

            ContentValues favoriteValues = new ContentValues();
            favoriteValues.put(COLUMN_FAVORITE_FRIEND_ID, friendId);
            favoriteValues.put(COLUMN_ITEM, item);

            long result = db.insert(TABLE_FAVORITES, null, favoriteValues);

            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public Cursor getAllFriendInformation(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = String.format(
                "SELECT f.%s, f.%s, f.%s, fav.%s " +
                        "FROM %s f " +
                        "JOIN %s fav ON f.%s = fav.%s " +
                        "WHERE f.%s = ? " +
                        "ORDER BY " +
                        "CASE " +
                        "   WHEN (CAST(substr(f.%s, 1, 2) AS INTEGER) > strftime('%%m', 'now') OR " +
                        "        (CAST(substr(f.%s, 1, 2) AS INTEGER) = strftime('%%m', 'now') AND " +
                        "         CAST(substr(f.%s, 4, 2) AS INTEGER) >= strftime('%%d', 'now'))) " +
                        "   THEN 1 ELSE 2 END, " +
                        "CAST(substr(f.%s, 1, 2) AS INTEGER), CAST(substr(f.%s, 4, 2) AS INTEGER)",
                COLUMN_FRIEND_ID, COLUMN_FRIEND_NAME, COLUMN_BIRTHDAY, COLUMN_ITEM,
                TABLE_FRIENDS, TABLE_FAVORITES,
                COLUMN_FRIEND_ID, COLUMN_FAVORITE_FRIEND_ID,
                COLUMN_FRIEND_USER_ID,
                COLUMN_BIRTHDAY, COLUMN_BIRTHDAY, COLUMN_BIRTHDAY,
                COLUMN_BIRTHDAY, COLUMN_BIRTHDAY
        );

        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public boolean deleteFriend(int friendId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_FRIENDS, COLUMN_FRIEND_ID + "=?", new String[]{String.valueOf(friendId)});
        db.close();
        return result > 0;
    }

}
