package com.example.quizapp.adminActivities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.R;
import com.example.quizapp.adminFragments.AssignStudentFragment;
import com.example.quizapp.classes.Course;
import com.example.quizapp.classes.Student;
import com.example.quizapp.databinding.ActivityAssignStudentToCourseBinding;
import com.example.quizapp.listeners.OnLoadedListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class AssignStudentToCourse extends BaseAdminActivity {//Checked

    private FragmentManager fragmentManager;

    private ActivityAssignStudentToCourseBinding binding;
    private List<Student> students;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAssignStudentToCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fragmentManager = getSupportFragmentManager();
        course = (Course) getIntent().getSerializableExtra("course");
        binding.title.setText("Students Assignment: " + course.getCourseCode());
        setupToolbar(R.id.toolbar);
        loadStudents();
    }

    private void loadStudents() {
        binding.progressLoader.setVisibility(View.VISIBLE);
        SplashActivity.dbHelper.getAllStudents(new OnLoadedListener<Student>() {
            @Override
            public void onLoaded(List<Student> items) {
                students = items;
                for(Student s : students){
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    AssignStudentFragment fragment = new AssignStudentFragment(s, course);
                    fragmentTransaction.add(R.id.frag_container, fragment);
                    fragmentTransaction.commit();
                }
                binding.progressLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(),"Couldn't load students",Snackbar.LENGTH_LONG).show();
                binding.progressLoader.setVisibility(View.GONE);
            }
        });

    }
}