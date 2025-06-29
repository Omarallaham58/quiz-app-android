package com.example.quizapp.adminActivities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.R;
import com.example.quizapp.classes.Student;
import com.example.quizapp.databinding.ActivityUpdateStudentBinding;
import com.example.quizapp.listeners.OnModifyListener;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.util.Patterns;

public class UpdateStudentActivity extends BaseAdminActivity {//Checked

    private ActivityUpdateStudentBinding binding;
    private Student student;
    private Date selectedDob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(R.id.toolbar);

        student = (Student) getIntent().getSerializableExtra("student");

        binding.dateDob.setOnClickListener(v -> showCalendar(binding.dateDob));
        binding.btUpdate.setOnClickListener(v -> updateStudent());

        loadInfo();
    }

    @SuppressLint("DefaultLocale")
    private void loadInfo() {
        binding.etFileNumber.setText(String.valueOf(student.getFileNumber()));
        binding.etFirstName.setText(student.getFirstName());
        binding.etLastName.setText(student.getLastName());
        binding.etEmail.setText(student.getEmail());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        if (student.getDob() != null) {
            String dobStr = formatter.format(student.getDob());
            binding.dateDob.setText(dobStr);
            selectedDob = Date.valueOf(dobStr);
        }

        binding.etUsername.setText(student.getUsername());
        binding.etPassword.setText(student.getPassword());
        binding.etGpa.setText(String.format("%.2f",student.getGpa()));
    }

    private void showCalendar(EditText field) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    field.setText(formattedDate);
                    selectedDob = Date.valueOf(formattedDate);

                    //  Validate age
                    Calendar dobCal = Calendar.getInstance();
                    dobCal.set(selectedYear, selectedMonth, selectedDay);
                    int age = year - selectedYear;
                    if (calendar.get(Calendar.DAY_OF_YEAR) < dobCal.get(Calendar.DAY_OF_YEAR)) {
                        age--;
                    }

                    if (age < 18) {
                        field.setError("Student must be at least 18 years old");
                        selectedDob = null;
                        Snackbar.make(binding.getRoot(), "DOB not allowed: Age must be at least 18", Snackbar.LENGTH_LONG).show();
                    } else {
                        field.setError(null);
                    }
                },
                year, month, day//to set the calendar to the current date
        );

        dialog.show();
    }

    private void updateStudent() {
        boolean isValid = true;

        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String gpaStr = binding.etGpa.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();


        if (firstName.isEmpty()) {
            binding.etFirstName.setError("Required");
            isValid = false;
        }
        if (lastName.isEmpty()) {
            binding.etLastName.setError("Required");
            isValid = false;
        }
        if (email.isEmpty()) {
            binding.etEmail.setError("Required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Invalid email format");
            isValid = false;
        }
        if (selectedDob == null) {
            binding.dateDob.setError("Invalid or underage DOB");
            isValid = false;
        }

        if(username.isEmpty()){

            binding.etUsername.setError("Required");
            isValid = false;
        }

        if(password.isEmpty()){

            binding.etPassword.setError("Required");
        }
//        if (gpaStr.isEmpty()) {
//            binding.etGpa.setError("Required");
//            isValid = false;
//        }

        if (!isValid) return;

        double gpa = Double.parseDouble(gpaStr);

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setDob(selectedDob);
        student.setGpa(gpa);
        student.setUsername(username);
        student.setPassword(password);

        SplashActivity.dbHelper.updateStudent(student, new OnModifyListener() {
            @Override
            public void onSuccess() {
                Snackbar.make(binding.getRoot(), "Student updated successfully", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onAlreadyExists() {

            }

            @Override
            public void onAlreadyExists(String msg) {

                Snackbar.make(binding.getRoot(),msg,Snackbar.LENGTH_LONG).show();


            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(), "Update failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
