package com.example.school_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "school.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_TEACHERS = "teachers";
    private static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";

    private static final String CREATE_TABLE_TEACHERS = "CREATE TABLE " + TABLE_TEACHERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_EMAIL + " TEXT);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TEACHERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_TEACHERS + " ADD COLUMN " + COLUMN_EMAIL + " TEXT;");
        }
    }

    public void addTeacher(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        db.insert(TABLE_TEACHERS, null, values);
        db.close();
    }

    public List<Teacher> getAllTeachers() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Teacher> teachers = new ArrayList<>();
        Cursor cursor = db.query(TABLE_TEACHERS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
                teachers.add(new Teacher(id, name, email));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return teachers;
    }
    public boolean deleteTeacher(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("teachers", "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updateTeacher(int id, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);

        // Update the row in the teachers table where the id matches
        int rowsAffected = db.update("teachers", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();

        return rowsAffected > 0;  // Return true if at least one row was updated
    }


}
