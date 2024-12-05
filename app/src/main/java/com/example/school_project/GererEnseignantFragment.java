package com.example.school_project;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GererEnseignantFragment extends Fragment {

    private EditText searchTeacherInput;
    private Button addTeacherButton;
    private RecyclerView teacherListRecyclerView;
    private TeacherAdapter teacherAdapter;
    private DBHelper dbHelper;

    public GererEnseignantFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gerer_enseignant, container, false);

        // Initialize UI components
        addTeacherButton = view.findViewById(R.id.add_teacher_button);
        teacherListRecyclerView = view.findViewById(R.id.teacher_list);
        searchTeacherInput = view.findViewById(R.id.search_teacher);
        dbHelper = new DBHelper(getContext());

        // Set up RecyclerView with the adapter
        teacherAdapter = new TeacherAdapter(getContext(), new ArrayList<>(), dbHelper);
        teacherListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        teacherListRecyclerView.setAdapter(teacherAdapter);

        // Load teachers from the database
        loadTeachers();

        // Open dialog to add a new teacher
        addTeacherButton.setOnClickListener(v -> openAddTeacherDialog());

        // Implement search filter with immediate update on typing and deleting letters
        searchTeacherInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterTeachers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return view;
    }

    private void openAddTeacherDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Teacher");

        // Inflate the custom layout for the dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_teacher, null);
        builder.setView(dialogView);

        EditText nameInput = dialogView.findViewById(R.id.dialog_teacher_name_input);
        EditText emailInput = dialogView.findViewById(R.id.dialog_teacher_email_input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();

            if (!name.isEmpty() && !email.isEmpty()) {
                dbHelper.addTeacher(name, email);
                loadTeachers();
            } else {
                Toast.makeText(getContext(), "Please enter both name and email", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void loadTeachers() {
        List<Teacher> teacherList = dbHelper.getAllTeachers();

        if (teacherList.isEmpty()) {
            Toast.makeText(getContext(), "No teachers found", Toast.LENGTH_SHORT).show();
        }

        teacherAdapter.updateData(teacherList);
    }

    private void filterTeachers(String query) {
        List<Teacher> filteredTeachers = new ArrayList<>();
        for (Teacher teacher : dbHelper.getAllTeachers()) {
            if (teacher.getName().toLowerCase().contains(query.toLowerCase()) || teacher.getEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredTeachers.add(teacher);
            }
        }
        teacherAdapter.updateData(filteredTeachers);
    }
}
