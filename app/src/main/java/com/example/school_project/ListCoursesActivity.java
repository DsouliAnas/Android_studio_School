package com.example.school_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListCoursesActivity extends AppCompatActivity implements CourseAdapter.OnCourseActionListener {

    private RecyclerView recyclerView;
    private ArrayList<Course> courseList;
    private CourseAdapter courseAdapter;
    private SQLiteDatabase db;
    private CourseDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_course_list);

        // Toolbar setup
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize RecyclerView and database
        recyclerView = findViewById(R.id.recyclerViewCourses);
        dbHelper = new CourseDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        courseList = getCoursesFromDatabase();
        courseAdapter = new CourseAdapter(courseList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(courseAdapter);

        // Set up search functionality
        EditText searchViewCourses = findViewById(R.id.searchViewCourses);
        searchViewCourses.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCourses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterCourses(String query) {
        ArrayList<Course> filteredList = new ArrayList<>();
        for (Course course : courseList) {
            if (course.getName().toLowerCase().contains(query.toLowerCase()) ||
                    course.getTeacher().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(course);
            }
        }
        courseAdapter = new CourseAdapter(filteredList, this);
        recyclerView.setAdapter(courseAdapter);
    }



    private ArrayList<Course> getCoursesFromDatabase() {
        ArrayList<Course> courses = new ArrayList<>();
        Cursor cursor = db.query("courses", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            int hours = cursor.getInt(cursor.getColumnIndexOrThrow("hours"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            String teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
            courses.add(new Course(name, hours, type, teacher));
        }
        cursor.close();
        return courses;
    }

    private void addCourse(String name, int hours, String type, String teacher) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("hours", hours);
        values.put("type", type);
        values.put("teacher", teacher);

        long result = db.insert("courses", null, values);
        String message = result != -1 ? "Course added successfully" : "Error adding course";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        refreshCourses();
    }

    private void refreshCourses() {
        courseList.clear();
        courseList.addAll(getCoursesFromDatabase());
        courseAdapter.notifyDataSetChanged();
    }

    private void showEditDialog(Course course) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_course, null);

        EditText editName = dialogView.findViewById(R.id.editCourseName);
        EditText editHours = dialogView.findViewById(R.id.editCourseHours);
        Spinner editTeacher = dialogView.findViewById(R.id.editCourseTeacher);

        editName.setText(course.getName());
        editHours.setText(String.valueOf(course.getHours()));

        List<String> teacherNames = getTeacherNamesFromDatabase();
        if (!teacherNames.isEmpty()) {
            ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherNames);
            teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            editTeacher.setAdapter(teacherAdapter);

            int position = teacherAdapter.getPosition(course.getTeacher());
            if (position >= 0) {
                editTeacher.setSelection(position);
            }
        } else {
            Toast.makeText(this, "No teachers available", Toast.LENGTH_SHORT).show();
        }

        new AlertDialog.Builder(this)
                .setTitle("Edit Course")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = editName.getText().toString().trim();
                    int newHours = Integer.parseInt(editHours.getText().toString().trim());
                    String newTeacher = editTeacher.getSelectedItem().toString();

                    ContentValues values = new ContentValues();
                    values.put("name", newName);
                    values.put("hours", newHours);
                    values.put("teacher", newTeacher);

                    db.update("courses", values, "name = ?", new String[]{course.getName()});
                    Toast.makeText(this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                    refreshCourses();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private List<String> getTeacherNamesFromDatabase() {
        List<String> teacherNames = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name FROM teachers", null);

        while (cursor.moveToNext()) {
            String teacherName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            teacherNames.add(teacherName);
        }

        cursor.close();
        return teacherNames;
    }

    private void showDeleteConfirmationDialog(Course course) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.delete("courses", "name = ?", new String[]{course.getName()});
                    Toast.makeText(this, "Course deleted successfully", Toast.LENGTH_SHORT).show();
                    refreshCourses();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onEditCourse(Course course) {
        showEditDialog(course);
    }

    @Override
    public void onDeleteCourse(Course course) {
        showDeleteConfirmationDialog(course);
    }

    private static class CourseDatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "school.db";
        private static final int DATABASE_VERSION = 3;

        public CourseDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE courses (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, hours INTEGER, type TEXT, teacher TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < 3) {
                db.execSQL("CREATE TABLE IF NOT EXISTS courses (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, hours INTEGER, type TEXT, teacher TEXT)");
            }
        }
    }
}
