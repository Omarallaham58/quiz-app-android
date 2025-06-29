package com.example.quizapp.adminActivities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.R;
import com.example.quizapp.classes.Course;
import com.example.quizapp.databinding.ActivityUpdateCourseBinding;
import com.example.quizapp.listeners.OnModifyListener;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//import java.util.Date;
import java.util.Locale;

public class UpdateCourseActivity extends BaseAdminActivity {//Checked

    private ActivityUpdateCourseBinding binding;
    private Course course;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUpdateCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        course = (Course) getIntent().getSerializableExtra("course");
        binding.btUpdate.setOnClickListener((v)->updateCourse());
        binding.dateStart.setOnClickListener((v)->{
            showCalendar(binding.dateStart);
            binding.dateStart.setError(null);
        });
        binding.dateEnd.setOnClickListener((v)->{
            showCalendar(binding.dateEnd);
            binding.dateEnd.setError(null);
        });
        loadInfo();
    }

    private void loadInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        binding.etCourseCode.setText(course.getCourseCode()+"");
        binding.etCourseName.setText(course.getCourseName());
        binding.etCredits.setText(course.getCredits()+"");
        binding.dateStart.setText(formatter.format(course.getStartDate()));
        binding.dateEnd.setText(formatter.format(course.getEndDate()));
        binding.etDuration.setText(course.getExamDuration()+"");
        binding.etNbQuestions.setText(course.getNbQuestions()+"");


        Date now = new Date(System.currentTimeMillis());
        if (!now.before(course.getStartDate()) && !now.after(course.getEndDate())) {
            //to notify the admin about possible synchronization issues
            binding.tvWarning.setVisibility(View.VISIBLE);
        } else {
            binding.tvWarning.setVisibility(View.GONE);
        }


    }

    private void showCalendar(EditText v) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view1, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    v.setText(date);
                },
                year, month, day
        );

        dialog.show();
    }

    private void updateCourse() {
        boolean isValid = true;

        String courseName = binding.etCourseName.getText().toString().trim();
        String creditsStr = binding.etCredits.getText().toString().trim();
        String durationStr = binding.etDuration.getText().toString().trim();
        String nbQuestionsStr = binding.etNbQuestions.getText().toString().trim();
        String startDateStr = binding.dateStart.getText().toString().trim();
        String endDateStr = binding.dateEnd.getText().toString().trim();

        if (courseName.isEmpty()) {
            binding.etCourseName.setError("Required");
            isValid = false;
        }
        if (creditsStr.isEmpty()) {
            binding.etCredits.setError("Required");
            isValid = false;
        }
        if (durationStr.isEmpty()) {
            binding.etDuration.setError("Required");
            isValid = false;
        }
        if (nbQuestionsStr.isEmpty()) {
            binding.etNbQuestions.setError("Required");
            isValid = false;
        }
        if (startDateStr.isEmpty()) {
            binding.dateStart.setError("Required");
            isValid = false;
        }
        if (endDateStr.isEmpty()) {
            binding.dateEnd.setError("Required");
            isValid = false;
        }

        if (!isValid) return;

        int credits = Integer.parseInt(creditsStr);
        int duration = Integer.parseInt(durationStr);
        int nbQuestions = Integer.parseInt(nbQuestionsStr);
        Date start = Date.valueOf(startDateStr);
        Date end = Date.valueOf(endDateStr);

        if (end.before(start)) {
            Snackbar.make(binding.getRoot(),"Start date can't be after end date!",Snackbar.LENGTH_LONG).show();
            isValid = false;
        }
        if (credits < 1) {
            binding.etCredits.setError("Must be > 0");
            isValid = false;
        }
        if (duration < 1) {
            binding.etDuration.setError("Must be > 0");
            isValid = false;
        }
        if (nbQuestions < 1) {
            binding.etNbQuestions.setError("Must be > 0");
            isValid = false;
        }

        if (!isValid) return;

        Course course = new Course();
        course.setCourseCode(this.course.getCourseCode());
        course.setCourseName(courseName);
        course.setCredits(credits);
        course.setStartDate(start);
        course.setEndDate(end);
        course.setExamDuration(duration);
        course.setNbQuestions(nbQuestions);

        SplashActivity.dbHelper.updateCourse(course, new OnModifyListener() {
            @Override
            public void onSuccess() {
                Snackbar.make(binding.getRoot(),"Course updated successfully",Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onAlreadyExists() {
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(),"Error: " + e.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });
    }
}