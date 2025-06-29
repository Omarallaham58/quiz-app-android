package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.adminActivities.ManageStudentsActivity;
import com.example.quizapp.classes.Constants;
import com.example.quizapp.databinding.ActivityLoginBinding;
import com.example.quizapp.listeners.OnLoadedListener;
import com.example.quizapp.classes.Student;
import com.example.quizapp.studentActivities.StudentActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class LoginActivity extends AppCompatActivity {//Checked

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(v -> processLogin());
    }

    private void processLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        if (username.isEmpty()) {
            binding.etUsername.setError("Username is required");
            return;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            return;
        }

        // Admin login check
        if (username.equals(Constants.ADMIN_USERNAME) && password.equals(Constants.ADMIN_PASSWORD)) {
            SessionManager.logIn(this, username, true);
            startActivity(new Intent(this, ManageStudentsActivity.class));
            finish();
            return;
        }

        // Otherwise, check student
        binding.progressBar.setVisibility(View.VISIBLE);
        SplashActivity.dbHelper.getStudentByUsername(username, new OnLoadedListener<Student>() {
            @Override
            public void onLoaded(List<Student> students) {
                binding.progressBar.setVisibility(View.GONE);
                if (students.isEmpty()) {
                    Snackbar.make(binding.getRoot(), "User not found", Snackbar.LENGTH_LONG).show();
                } else {
                    Student student = students.get(0);
                    if (student.getPassword().equals(password)) {
                        SessionManager.logIn(LoginActivity.this, student.getUsername(), false);
                        Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
                        intent.putExtra("student", student);
                        startActivity(intent);
                        finish();
                    } else {
                        Snackbar.make(binding.getRoot(), "Incorrect password", Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), "Login failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
