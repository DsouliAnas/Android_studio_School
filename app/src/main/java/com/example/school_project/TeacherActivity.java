package com.example.school_project;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TeacherActivity extends AppCompatActivity {

    private EditText teacherNameInput, teacherEmailInput;
    private Button addTeacherButton;
    private RecyclerView teacherListRecyclerView;
    private TeacherAdapter teacherAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gerer_enseignant);

        dbHelper = new DBHelper(this);

        addTeacherButton = findViewById(R.id.add_teacher_button);
        teacherListRecyclerView = findViewById(R.id.teacher_list);

        teacherAdapter = new TeacherAdapter(this, new ArrayList<>(), dbHelper);
        teacherListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teacherListRecyclerView.setAdapter(teacherAdapter);

        loadTeachers();

        addTeacherButton.setOnClickListener(v -> addTeacher());
    }

    private void addTeacher() {
        String teacherName = teacherNameInput.getText().toString();
        String teacherEmail = teacherEmailInput.getText().toString();

        if (!teacherName.isEmpty() && !teacherEmail.isEmpty()) {
            dbHelper.addTeacher(teacherName, teacherEmail);
            teacherNameInput.setText("");
            teacherEmailInput.setText("");
            loadTeachers();
        } else {
            Toast.makeText(this, "Please enter both name and email", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTeachers() {
        List<Teacher> teacherList = dbHelper.getAllTeachers();

        if (teacherList.isEmpty()) {
            Toast.makeText(this, "No teachers found", Toast.LENGTH_SHORT).show();
        }

        teacherAdapter.updateData(teacherList);
    }
}
