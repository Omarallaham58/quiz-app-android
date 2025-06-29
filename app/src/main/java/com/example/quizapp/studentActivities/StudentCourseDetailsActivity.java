package com.example.quizapp.studentActivities;

import android.os.Bundle;

import com.example.quizapp.R;
import com.example.quizapp.SplashActivity;
import com.example.quizapp.classes.Course;
import com.example.quizapp.databinding.ActivityStudentCourseDetailsBinding;
import com.example.quizapp.listeners.OnLoadedListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StudentCourseDetailsActivity extends StudentBaseActivity {//Checked

    private ActivityStudentCourseDetailsBinding binding;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentCourseDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        course = (Course) getIntent().getSerializableExtra("course");
        setupToolbar(R.id.toolbar, "Course Details");

        if (course != null) {
            loadCourseDetails();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (course != null) {
            SplashActivity.dbHelper.getCourseByCode(course.getCourseCode(), new OnLoadedListener<Course>() {
                @Override
                public void onLoaded(List<Course> items) {
                    if (!items.isEmpty()) {
                        course = items.get(0);
                        loadCourseDetails();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    // Handle failure silently
                }
            });
        }
    }

    private void loadCourseDetails() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        binding.tvCourseCode.setText(course.getCourseCode());
        binding.tvCourseName.setText(course.getCourseName());
        binding.tvCredits.setText(String.valueOf(course.getCredits()));
        binding.tvStartDate.setText(sdf.format(course.getStartDate()));
        binding.tvEndDate.setText(sdf.format(course.getEndDate()));
        binding.tvDuration.setText(String.valueOf(course.getExamDuration()));
        //binding.tvNbQuestions.setText(String.valueOf(course.getNbQuestions()));
    }
}
