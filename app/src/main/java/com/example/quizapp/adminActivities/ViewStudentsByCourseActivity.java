package com.example.quizapp.adminActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.R;
import com.example.quizapp.adapters.StudentByCourseAdapter;
import com.example.quizapp.classes.Course;
import com.example.quizapp.classes.Enrollment;
import com.example.quizapp.databinding.ActivityViewStudentsByCourseBinding;
import com.example.quizapp.listeners.OnLoadedListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ViewStudentsByCourseActivity extends BaseAdminActivity {//Checked

    private Course course;
    private StudentByCourseAdapter adapter;
    private List<Enrollment> enrollments;
    private ActivityViewStudentsByCourseBinding binding;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityViewStudentsByCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(R.id.toolbar);
        course = (Course) getIntent().getSerializableExtra("course");
        loadStudents();
    }

    private void loadStudents() {
        binding.headerRow.setVisibility(View.GONE);
        binding.progressLoader.setVisibility(View.VISIBLE);
        SplashActivity.dbHelper.getStudentsByCourse(course, new OnLoadedListener<Enrollment>() {
            @Override
            public void onLoaded(List<Enrollment> items) {
                enrollments = items;
                if(enrollments.isEmpty()) binding.tvEmpty.setVisibility(View.VISIBLE);
                else{
                   // Log.d("ENROLLMENTS", enrollments + "");
                    adapter = new StudentByCourseAdapter(context, enrollments);
                    binding.studentListView.setAdapter(adapter);
                    binding.headerRow.setVisibility(View.VISIBLE);
                }
                binding.progressLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(),e.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });
    }

}