package com.example.quizapp.adminActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.R;
import com.example.quizapp.classes.Student;
import com.example.quizapp.databinding.ActivityStudentDetailsBinding;
import com.example.quizapp.listeners.OnLoadedListener;
import com.example.quizapp.listeners.OnModifyListener;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StudentDetailsActivity extends BaseAdminActivity {//Checked

    private ActivityStudentDetailsBinding binding;
    public Student student;
    public Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(R.id.toolbar);

        student = (Student) getIntent().getSerializableExtra("student");

        binding.btnUpdateStudent.setOnClickListener(v -> switchToUpdateStudent(student));
        binding.btnDeleteStudent.setOnClickListener(v -> deleteStudent(student));
    }

    @SuppressLint("DefaultLocale")
    private void loadStudentInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        binding.tvFileNumber.setText(String.valueOf(student.getFileNumber()));
        binding.tvFullName.setText(student.getFirstName() + " " + student.getLastName());
        binding.tvEmail.setText(student.getEmail());
        binding.tvDob.setText(sdf.format(student.getDob()));
        binding.tvUsername.setText(student.getUsername());
        binding.tvPassword.setText(student.getPassword());
        binding.tvGpa.setText(String.format("%.2f",student.getGpa()));
    }

    private void switchToUpdateStudent(Student student) {
        Intent intent = new Intent(this, UpdateStudentActivity.class);
        intent.putExtra("student", student);
        startActivity(intent);
    }

    private void deleteStudent(Student student) {
        SplashActivity.dbHelper.deleteStudent(student, new OnModifyListener() {
            @Override
            public void onSuccess() {
                Snackbar.make(binding.getRoot(), "Student deleted successfully", Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(context, ManageStudentsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAlreadyExists() {}

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(), "Deletion failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SplashActivity.dbHelper.getStudentByFileNumber(student.getFileNumber(), new OnLoadedListener<Student>() {
            @Override
            public void onLoaded(List<Student> items) {
                if (!items.isEmpty()) {
                    student = items.get(0);
                    loadStudentInfo();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(), "Error loading student: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
