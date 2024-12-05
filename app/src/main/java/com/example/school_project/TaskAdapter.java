// TaskAdapter.java

package com.example.school_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskClickListener onTaskClickListener;

    public interface OnTaskClickListener {
        void onTaskDoneChanged(Task task);
        void onEditTask(Task task, int position);
        void onDeleteTask(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener onTaskClickListener) {
        this.taskList = taskList;
        this.onTaskClickListener = onTaskClickListener;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskNameTextView.setText(task.getName());
        holder.taskDescTextView.setText(task.getDescription());
        holder.taskDateTextView.setText(task.getDate());
        holder.checkBoxDone.setChecked(task.isDone());

        holder.checkBoxDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setDone(isChecked);
            onTaskClickListener.onTaskDoneChanged(task);
        });

        holder.editButton.setOnClickListener(v -> onTaskClickListener.onEditTask(task, position));
        holder.deleteButton.setOnClickListener(v -> onTaskClickListener.onDeleteTask(task));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView, taskDescTextView, taskDateTextView;
        CheckBox checkBoxDone;
        Button editButton, deleteButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.taskNameTextView);
            taskDescTextView = itemView.findViewById(R.id.taskDescTextView);
            taskDateTextView = itemView.findViewById(R.id.taskDateTextView);
            checkBoxDone = itemView.findViewById(R.id.checkBoxDone);
            editButton = itemView.findViewById(R.id.btnEditTask);
            deleteButton = itemView.findViewById(R.id.btnDeleteTask);
        }
    }
}
