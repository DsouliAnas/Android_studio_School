package com.example.school_project;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;

public class GererCoursFragment extends Fragment {
    private EditText editTextCourseName, editTextCourseHours;
    private Spinner spinnerTeacher;
    private RecyclerView recyclerView;
    private SQLiteDatabase db;

    // Define listener for course actions (edit/delete)
    private CourseAdapter.OnCourseActionListener onCourseActionListener = new CourseAdapter.OnCourseActionListener() {
        @Override
        public void onEditCourse(Course course) {
            showEditDialog(course); // Handle edit action: open dialog to edit course
        }

        @Override
        public void onDeleteCourse(Course course) {
            showDeleteConfirmationDialog(course); // Handle delete action: show confirmation dialog
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gerer_cours, container, false);

        // Initialize UI elements
        initializeUIElements(view);

        // Initialize database
        CourseDatabaseHelper dbHelper = new CourseDatabaseHelper(getContext());
        db = dbHelper.getWritableDatabase();

        // Populate teacher spinner with data from the database
        populateTeacherSpinner();

        // Set button listeners
        setButtonListeners(view);

        return view;
    }


    // Initialize UI elements (EditText, Spinner, Buttons, RecyclerView)
    private void initializeUIElements(View view) {
        editTextCourseName = view.findViewById(R.id.editTextCourseName);
        editTextCourseHours = view.findViewById(R.id.editTextCourseHours);
        spinnerTeacher = view.findViewById(R.id.spinnerTeacher);
        recyclerView = view.findViewById(R.id.recyclerViewCourses);
    }

    // Populate the teacher spinner with teacher names from the database
    private void populateTeacherSpinner() {
        ArrayList<String> teachers = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name FROM teachers", null);
        if (cursor.moveToFirst()) {
            do {
                String teacherName = cursor.getString(cursor.getColumnIndex("name"));
                teachers.add(teacherName);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, teachers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(adapter);
    }

    // Set listeners for buttons
    private void setButtonListeners(View view) {
        Button buttonAddCourse = view.findViewById(R.id.buttonAddCourse);
        Button buttonListCourses = view.findViewById(R.id.buttonListCourses);

        buttonAddCourse.setOnClickListener(v -> addCourse());
        buttonListCourses.setOnClickListener(v -> openCourseListActivity());
    }


    // Add a new course to the database
    private void addCourse() {
        String courseName = editTextCourseName.getText().toString().trim();
        String courseHours = editTextCourseHours.getText().toString().trim();
        String teacher = spinnerTeacher.getSelectedItem().toString();

        if (courseName.isEmpty() || courseHours.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("name", courseName);
        values.put("hours", Integer.parseInt(courseHours));
        values.put("teacher", teacher);
        values.put("type", "Default Type");

        long result = db.insert("courses", null, values);
        String message = result != -1 ? "Course added successfully" : "Error adding course";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Open the activity to list all courses
    private void openCourseListActivity() {
        Intent intent = new Intent(getActivity(), ListCoursesActivity.class);
        startActivity(intent);
    }

    // Retrieve all courses from the database and display them
    private void listCourses() {
        Cursor cursor = db.query("courses", null, null, null, null, null, null);
        ArrayList<Course> courseList = new ArrayList<>();

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            int hours = cursor.getInt(cursor.getColumnIndexOrThrow("hours"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            String teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
            courseList.add(new Course(name, hours, type, teacher));
        }
        cursor.close();

        CourseAdapter adapter = new CourseAdapter(courseList, onCourseActionListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    // Show dialog to edit course details
    private void showEditDialog(Course course) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_course, null);

        EditText editName = dialogView.findViewById(R.id.editCourseName);
        EditText editHours = dialogView.findViewById(R.id.editCourseHours);
        Spinner editTeacher = dialogView.findViewById(R.id.editCourseTeacher);

        editName.setText(course.getName());
        editHours.setText(String.valueOf(course.getHours()));

        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"Teacher 1", "Teacher 2", "Teacher 3"});
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTeacher.setAdapter(teacherAdapter);
        editTeacher.setSelection(teacherAdapter.getPosition(course.getTeacher()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Course")
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
                    Toast.makeText(getContext(), "Course updated successfully", Toast.LENGTH_SHORT).show();
                    listCourses(); // Refresh the course list
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Show confirmation dialog before deleting a course
    private void showDeleteConfirmationDialog(Course course) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.delete("courses", "name = ?", new String[]{course.getName()});
                    Toast.makeText(getContext(), "Course deleted successfully", Toast.LENGTH_SHORT).show();
                    listCourses(); // Refresh the course list
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Database helper class to manage the courses and teachers tables
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
