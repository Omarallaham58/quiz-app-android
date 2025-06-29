package com.example.quizapp.adminActivities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.R;
import com.example.quizapp.classes.Course;
import com.example.quizapp.classes.Enrollment;
import com.example.quizapp.databinding.ActivityCourseHistogramBinding;
import com.example.quizapp.listeners.OnLoadedListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CourseHistogramActivity extends BaseAdminActivity {//Checked

    private ActivityCourseHistogramBinding binding;
    private Course course;
    private List<Enrollment> enrollments;
    private int []grades = new int[]{0,0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseHistogramBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(R.id.toolbar);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        course = (Course) getIntent().getSerializableExtra("course");
       // Log.d("TEST", "course fetched succ." + course);
        binding.title.setText("Grades Histogram for \"" + course.getCourseCode() + "\" course");
       // Log.d("TEST", "binding succ.");
        loadHistoInfo();
    }

    private void loadHistoInfo() {
        SplashActivity.dbHelper.getCourseHistoInfo(course, new OnLoadedListener() {
            @Override
            public void onLoaded(List items) {
                enrollments = items;
            //    Log.d("TEST", "course fetched succ.2");
                prepareData();
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(), "Error fetching enrollments: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void prepareData() {
        for(Enrollment e : enrollments){
            double grade = e.getGrade();
            if (grade>=0 && grade <= 25) grades[0]++;
            else if (grade>25 && grade <= 50) grades[1]++;
            else if (grade>50 && grade <= 75) grades[2]++;
            else if (grade>75 && grade <= 100) grades[3]++;
        }
        binding.histoView.setData(grades);
      //  Log.d("TEST", "course fetched succ.3" );
    }
}