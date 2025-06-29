package com.example.quizapp.adminActivities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.R;
import com.example.quizapp.classes.Student;
import com.example.quizapp.databinding.ActivityAddStudentBinding;
import com.example.quizapp.listeners.OnModifyListener;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Date;
import java.util.Calendar;

public class AddStudentActivity extends BaseAdminActivity {//Checked

    private ActivityAddStudentBinding binding;
    private Date selectedDob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(R.id.toolbar);

        binding.dateDob.setOnClickListener(v -> showCalendar(binding.dateDob));
        binding.btAdd.setOnClickListener(v -> addStudent());
    }

    private void showCalendar(EditText dateField) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    String formattedDate = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    dateField.setText(formattedDate);
                    selectedDob = Date.valueOf(formattedDate);

                    // Age validation
                    Calendar today = Calendar.getInstance();
                    Calendar dobCal = Calendar.getInstance();
                    dobCal.set(year1, month1, dayOfMonth);
                    int age = today.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR);
                    if (today.get(Calendar.DAY_OF_YEAR) < dobCal.get(Calendar.DAY_OF_YEAR)) {
                        age--;
                    }

                    if (age < 18) {
                        dateField.setError("Student must be at least 18 years old");
                        selectedDob = null;
                        Snackbar.make(binding.getRoot(), "DOB not allowed: Age must be at least 18", Snackbar.LENGTH_LONG).show();
                    } else {
                        dateField.setError(null);
                    }
                },
                year, month, day
        );
        dialog.show();
    }


    private void addStudent() {
        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();

        boolean isValid = true;

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

        if (!isValid) return;

        Student student = new Student(firstName, lastName, selectedDob, email);
        SplashActivity.dbHelper.addStudent(student, new OnModifyListener() {
            @Override
            public void onSuccess() {
                Snackbar.make(binding.getRoot(), "Student added!", Snackbar.LENGTH_LONG).show();
                clearForm();
            }

            @Override
            public void onAlreadyExists() {
                Snackbar.make(binding.getRoot(), "A student with this email already exists", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onAlreadyExists(String msg) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(), "Failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }




    private void clearForm() {
        binding.etFirstName.setText("");
        binding.etLastName.setText("");
        binding.etEmail.setText("");
        binding.dateDob.setText("");
        selectedDob = null;
        binding.etFirstName.requestFocus();
    }
}
