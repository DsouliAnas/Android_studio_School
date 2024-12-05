package com.example.school_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private List<Teacher> teacherList;
    private List<Teacher> filteredTeacherList;
    private Context context;
    private DBHelper dbHelper;

    public TeacherAdapter(Context context, List<Teacher> teacherList, DBHelper dbHelper) {
        this.context = context;
        this.teacherList = teacherList;
        this.filteredTeacherList = new ArrayList<>(teacherList);  // Initialize filtered list with all teachers
        this.dbHelper = dbHelper;
    }

    @Override
    public TeacherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_item, parent, false);
        return new TeacherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TeacherViewHolder holder, int position) {
        Teacher teacher = filteredTeacherList.get(position);
        holder.teacherNameTextView.setText(teacher.getName());
        holder.teacherEmailTextView.setText(teacher.getEmail());

        final int teacherId = teacher.getId();

        holder.editButton.setOnClickListener(v -> openEditTeacherDialog(teacher));

        // Find and set click listener for the delete button
        Button deleteButton = holder.itemView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to delete " + teacher.getName() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        if (teacherId != -1) {
                            dbHelper.deleteTeacher(teacherId);
                            teacherList.remove(teacher);
                            filteredTeacherList.remove(teacher);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "Error: Teacher could not be deleted.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }


    @Override
    public int getItemCount() {
        return filteredTeacherList.size();
    }


    public void updateData(List<Teacher> teacherList) {
        this.teacherList = teacherList;
        this.filteredTeacherList = new ArrayList<>(teacherList);  // Update the filtered list as well
        notifyDataSetChanged();  // Refresh the adapter
    }

    // Get the filtered teacher list for search
    public List<Teacher> getTeacherList() {
        return filteredTeacherList;
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView teacherNameTextView;
        TextView teacherEmailTextView;
        Button editButton;

        public TeacherViewHolder(View itemView) {
            super(itemView);
            teacherNameTextView = itemView.findViewById(R.id.teacher_name);
            teacherEmailTextView = itemView.findViewById(R.id.teacher_email);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }

    private void openEditTeacherDialog(Teacher teacher) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Teacher");

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_teacher, null);
        builder.setView(dialogView);

        EditText nameInput = dialogView.findViewById(R.id.dialog_teacher_name_input);
        EditText emailInput = dialogView.findViewById(R.id.dialog_teacher_email_input);

        // Set current name and email in the dialog fields
        nameInput.setText(teacher.getName());
        emailInput.setText(teacher.getEmail());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();

            if (!name.isEmpty() && !email.isEmpty()) {
                dbHelper.updateTeacher(teacher.getId(), name, email);
                loadTeachers();
            } else {
                Toast.makeText(context, "Please enter both name and email", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void loadTeachers() {
        List<Teacher> teachers = dbHelper.getAllTeachers();  // Make sure this method exists in DBHelper
        teacherList.clear();
        teacherList.addAll(teachers);
        filteredTeacherList.clear();
        filteredTeacherList.addAll(teachers);
        notifyDataSetChanged();
    }
}
