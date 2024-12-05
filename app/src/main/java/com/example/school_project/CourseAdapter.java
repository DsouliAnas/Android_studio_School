package com.example.school_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private OnCourseActionListener onCourseActionListener;

    public interface OnCourseActionListener {
        void onEditCourse(Course course);
        void onDeleteCourse(Course course);
    }

    public CourseAdapter(List<Course> courseList, OnCourseActionListener onCourseActionListener) {
        this.courseList = courseList;
        this.onCourseActionListener = onCourseActionListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.nameTextView.setText(course.getName());
        holder.hoursTextView.setText(String.valueOf(course.getHours()));
        holder.teacherTextView.setText(course.getTeacher());

        holder.editButton.setOnClickListener(v -> onCourseActionListener.onEditCourse(course));
        holder.deleteButton.setOnClickListener(v -> onCourseActionListener.onDeleteCourse(course));
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, hoursTextView, teacherTextView;
        Button editButton, deleteButton;

        public CourseViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewCourseName);
            hoursTextView = itemView.findViewById(R.id.textViewCourseHours);
            teacherTextView = itemView.findViewById(R.id.textViewTeacher);
            editButton = itemView.findViewById(R.id.buttonEditCourse);
            deleteButton = itemView.findViewById(R.id.buttonDeleteCourse);
        }
    }
}
