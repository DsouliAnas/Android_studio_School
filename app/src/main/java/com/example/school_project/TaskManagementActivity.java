package com.example.school_project;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TaskManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TaskDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        dbHelper = new TaskDatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Show the back button
        getSupportActionBar().setHomeButtonEnabled(true);

        taskList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskDoneChanged(Task task) {
                updateTaskDone(task);
            }

            @Override
            public void onEditTask(Task task, int position) {
                showEditTaskDialog(task, position);
            }

            @Override
            public void onDeleteTask(Task task) {
                deleteTask(task);
            }
        });
        recyclerView.setAdapter(taskAdapter);

        Button addTaskButton = findViewById(R.id.btnAddTask);
        addTaskButton.setOnClickListener(v -> showAddTaskDialog());

        loadTasks();
    }

    private void loadTasks() {
        taskList.clear();
        taskList.addAll(dbHelper.getAllTasks());
        taskAdapter.notifyDataSetChanged();
    }

    private void showAddTaskDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        final EditText taskNameEditText = dialogView.findViewById(R.id.etTaskName);
        final EditText taskDescEditText = dialogView.findViewById(R.id.etTaskDescription);
        final EditText taskDateEditText = dialogView.findViewById(R.id.etTaskDate);

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskManagementActivity.this);
        builder.setView(dialogView)
                .setTitle("Add Task")
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = taskNameEditText.getText().toString();
                    String description = taskDescEditText.getText().toString();
                    String date = taskDateEditText.getText().toString();
                    if (!name.isEmpty() && !description.isEmpty() && !date.isEmpty()) {
                        Task newTask = new Task(0, name, description, date, false);
                        dbHelper.addTask(newTask);
                        loadTasks();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(TaskManagementActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.create().show();
    }

    private void showEditTaskDialog(final Task task, final int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_task, null);
        final EditText taskNameEditText = dialogView.findViewById(R.id.etTaskName);
        final EditText taskDescEditText = dialogView.findViewById(R.id.etTaskDescription);
        final EditText taskDateEditText = dialogView.findViewById(R.id.etTaskDate);

        taskNameEditText.setText(task.getName());
        taskDescEditText.setText(task.getDescription());
        taskDateEditText.setText(task.getDate());

        new AlertDialog.Builder(TaskManagementActivity.this)
                .setTitle("Edit Task")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = taskNameEditText.getText().toString();
                    String description = taskDescEditText.getText().toString();
                    String date = taskDateEditText.getText().toString();

                    if (!name.isEmpty() && !description.isEmpty() && !date.isEmpty()) {
                        task.setName(name);
                        task.setDescription(description);
                        task.setDate(date);

                        dbHelper.updateTask(task);
                        taskAdapter.notifyItemChanged(position);  // Update only the modified task
                        Toast.makeText(TaskManagementActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TaskManagementActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .create()
                .show();
    }

    private void updateTaskDone(Task task) {
        dbHelper.updateTask(task);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate back to the HomeActivity or the previous activity
                onBackPressed();  // Calls finish() and pops the activity stack
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteTask(final Task task) {
        new AlertDialog.Builder(TaskManagementActivity.this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteTask(task);
                    loadTasks();  // Reload tasks after deletion
                    Toast.makeText(TaskManagementActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
