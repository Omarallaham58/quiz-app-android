package com.example.quizapp.adminActivities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.R;
import com.example.quizapp.classes.Course;
import com.example.quizapp.databinding.ActivityAddCourseBinding;
import com.example.quizapp.listeners.OnModifyListener;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Date;
import java.util.Calendar;
//import java.util.Date;

public class AddCourseActivity extends BaseAdminActivity {//Checked

    private ActivityAddCourseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(R.id.toolbar);
        binding.btAdd.setOnClickListener((v)->addCourse());
        binding.dateStart.setOnClickListener((v)->{
            showCalendar(binding.dateStart);
            binding.dateStart.setError(null);
        });
        binding.dateEnd.setOnClickListener((v)->{
            showCalendar(binding.dateEnd);
            binding.dateEnd.setError(null);
        });
    }

    public void showCalendar(EditText v){
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

    private void addCourse() {
        boolean isValid = true;

        String courseCode = binding.etCourseCode.getText().toString().trim();
        String courseName = binding.etCourseName.getText().toString().trim();
        String creditsStr = binding.etCredits.getText().toString().trim();
        String durationStr = binding.etDuration.getText().toString().trim();
        String nbQuestionsStr = binding.etNbQuestions.getText().toString().trim();
        String startDateStr = binding.dateStart.getText().toString().trim();
        String endDateStr = binding.dateEnd.getText().toString().trim();

        if (courseCode.isEmpty()) {
            binding.etCourseCode.setError("Required");
            isValid = false;
        }
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
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setCredits(credits);
        course.setStartDate(start);
        course.setEndDate(end);
        course.setExamDuration(duration);
        course.setNbQuestions(nbQuestions);

        SplashActivity.dbHelper.addCourse(course, new OnModifyListener() {
            @Override
            public void onSuccess() {
                Snackbar.make(binding.getRoot(),"Course added successfully",Snackbar.LENGTH_LONG).show();
                binding.etCourseCode.setText("");
                binding.etCourseName.setText("");
                binding.etCredits.setText("");
                binding.dateStart.setText("");
                binding.dateEnd.setText("");
                binding.etDuration.setText("");
                binding.etNbQuestions.setText("");
                binding.etCourseCode.requestFocus();
            }

            @Override
            public void onAlreadyExists() {
                Snackbar.make(binding.getRoot(),"Course already exists",Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(),"Error: " + e.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });
    }
}