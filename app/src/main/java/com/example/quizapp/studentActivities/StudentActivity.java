//package com.example.quizapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.quizapp.adminActivities.ManageCoursesActivity;
//import com.example.quizapp.adminActivities.ManageStudentsActivity;
//import com.example.quizapp.classes.Student;
//import com.example.quizapp.databinding.ActivityStudentBinding;
//
//public class StudentActivity extends AppCompatActivity {
//
//    private Student student;
//    private ActivityStudentBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityStudentBinding.inflate(getLayoutInflater());
//        EdgeToEdge.enable(this);
//        setContentView(binding.getRoot());
//        setupToolbar(R.id.toolbar);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        student = (Student)getIntent().getSerializableExtra("student");
//        if(student != null)
//        binding.tvWelcome.setText("Welcome, " + student.getFirstName() + "!");
//
//    }
//
//
//    protected void setupToolbar(int toolbarId) {
//        Toolbar toolbar = findViewById(toolbarId);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }
//    }
//// using the admin tool bar for testing only
//    // a student menu to be created
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_admin, menu);
//        return true;
//    }
//
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        if (MainActivity.dbHelper == null) {
////            MainActivity.dbHelper = new DBHelper();
////        }
////    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId()==R.id.item_students) {
//            //Log.d("TAG_Manage_Students", "item_students");
//            Intent intent = new Intent(this, ManageStudentsActivity.class);
//            startActivity(intent);
//        }
//        if (item.getItemId()==R.id.item_courses) {
//            Intent intent = new Intent(this, ManageCoursesActivity.class);
//            startActivity(intent);
//        }
//        if (item.getItemId()==R.id.item_logout) {
//            //Log.d("TAG_Account_Admin", "item_logout");
//            SessionManager.logout(this);
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }
//        return true;
//    }
//}



// StudentActivity.java
package com.example.quizapp.studentActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.quizapp.R;
import com.example.quizapp.SessionManager;
import com.example.quizapp.SplashActivity;
import com.example.quizapp.classes.Course;
import com.example.quizapp.classes.Enrollment;
import com.example.quizapp.classes.Student;
import com.example.quizapp.databinding.ActivityStudentBinding;
import com.example.quizapp.listeners.OnLoadedListener;
import com.example.quizapp.listeners.OnModifyListener;
import com.example.quizapp.studentFragments.StudentCourseFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudentActivity extends StudentBaseActivity {//Checked

    private ActivityStudentBinding binding;
    private Student currentStudent;
    private List<Enrollment> enrollments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currentStudent = (Student) getIntent().getSerializableExtra("student");
       // Log.d("Student",currentStudent.toString());
        setupToolbar(R.id.toolbar,currentStudent.getFirstName());



        if (currentStudent == null) {
            Snackbar.make(binding.getRoot(), "No student data found", Snackbar.LENGTH_LONG).show();
            SessionManager.logout(this);
            startActivity(new Intent(this, SplashActivity.class));
            finish();
            return;
        }

        //loadEnrollments();
    }


    private void loadEnrollments() {
        // Show progress bar
        binding.progressBar.setVisibility(View.VISIBLE);

        // Clear existing fragments
        binding.courseContainer.removeAllViews();
        enrollments.clear();

        SplashActivity.dbHelper.getEnrollmentsByStudentId(currentStudent.getFileNumber(), new OnLoadedListener<Enrollment>() {


            @Override
            public void onLoaded(List<Enrollment> items) {
                enrollments = items;

                double totalGradePoints = 0;
                int totalCredits = 0;

                List<Enrollment> enrollmentsToUpdate = new ArrayList<>();
                Date now = new Date();

                for (Enrollment e : enrollments) {
                    Course course = e.getCourse();

                    // Handle missed exam case (grade == -1 and past end date)
                    if (e.getGrade() == -1 && now.after(course.getEndDate())) {
                        e.setGrade(0);
                        e.setPassed(false);
                        e.setLastExamEndDate(course.getEndDate());
                        enrollmentsToUpdate.add(e); // update the enrollments later(no need for synch
                        //here)
                    }

                    // GPA calculation (after potential update (locally))
                    if (e.getGrade() >= 0) {
                        int credits = course.getCredits();
                        totalGradePoints += e.getGrade() * credits;
                        totalCredits += credits;
                    }

                    // Add fragment
                    Fragment frag = new StudentCourseFragment(course, e);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.course_container, frag);
                    ft.commit();
                }

                double gpa = totalCredits == 0 ? 0 : totalGradePoints / totalCredits;
               // binding.tvGpa.setText(String.format("GPA: %.2f", gpa));
                binding.gpaIndicator.setValue((float)gpa);
                binding.progressBar.setVisibility(View.GONE);

                // Update student GPA if needed
                if (Math.abs(currentStudent.getGpa() - gpa) > 0.01) {
                    currentStudent.setGpa(gpa);
                    SplashActivity.dbHelper.updateStudent(currentStudent, new OnModifyListener() {

                        @Override public void onSuccess() {
                        //    Log.d("GPA", "Student GPA updated.");
                        }

                        @Override public void onAlreadyExists() {
                           // Log.d("GPA", "GPA update conflict.");
                        }

                        @Override public void onFailure(Exception e) {
                          //  Log.d("GPA", "GPA update failed: " + e.getMessage());
                        }
                    });
                }

                // Update modified enrollments (missed exams)
                for (Enrollment missed : enrollmentsToUpdate) {
                    SplashActivity.dbHelper.updateEnrollment(missed, new OnLoadedListener<Boolean>() {
                        @Override public void onLoaded(List<Boolean> result) {
                           // Log.d("Exam", "Enrollment updated due to missed exam.");
                        }

                        @Override public void onFailure(Exception e) {
                           // Log.e("Exam", "Failed to update missed exam enrollment: " + e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), "Failed to load enrollments", Snackbar.LENGTH_LONG).show();
            }

            });
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadEnrollments();
    }
}
