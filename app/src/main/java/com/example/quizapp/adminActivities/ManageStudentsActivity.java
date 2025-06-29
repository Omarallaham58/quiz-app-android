package com.example.quizapp.adminActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.R;
import com.example.quizapp.adminFragments.StudentFragment;
import com.example.quizapp.classes.Student;
import com.example.quizapp.databinding.ActivityManageStudentsBinding;
import com.example.quizapp.listeners.OnLoadedListener;
import com.example.quizapp.listeners.OnModifyListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ManageStudentsActivity extends BaseAdminActivity {//Checked

    private FragmentManager fragmentManager;
    public List<Student> students;
    public ActivityManageStudentsBinding binding;
    private String TAG_PREFIX = "student_";
    private int nbLines = 0;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageStudentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(R.id.toolbar);

        fragmentManager = getSupportFragmentManager();

        binding.btnAddStudent.setOnClickListener(v -> switchToAddStudentActivity());
    }

    private void switchToAddStudentActivity() {
       // Log.d("Student","SWITCH_TO_ADD");
        Intent intent = new Intent(this, AddStudentActivity.class);
        startActivity(intent);
    }

    public void loadAllStudents() {
        binding.progressLoader.setVisibility(View.VISIBLE);

        // Clear old fragments
        List<Fragment> existingFragments = fragmentManager.getFragments();
        FragmentTransaction clearTransaction = fragmentManager.beginTransaction();
        for (Fragment frag : existingFragments) {
            if (frag != null && frag.getTag() != null && frag.getTag().startsWith(TAG_PREFIX)) {
                clearTransaction.remove(frag);
            }
        }
        clearTransaction.commitNow();
        nbLines = 0;

        SplashActivity.dbHelper.getAllStudents(new OnLoadedListener<Student>() {
            @Override
            public void onLoaded(List<Student> listStudents) {
                students = listStudents;
                if (students == null || students.isEmpty()) {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                } else {
                   // Log.d("STUDENTS", students + "");
                    for (Student s : students) {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        StudentFragment fragment = new StudentFragment(s, context);
                        fragment.registerDeleteListener(tag -> deleteFragment(tag, s));
                        String tag1 = TAG_PREFIX + nbLines++;
                        fragmentTransaction.add(R.id.fragContainer, fragment, tag1);
                        fragmentTransaction.commit();
                    }
                }
                binding.progressLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void deleteFragment(String tag, Student student) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            SplashActivity.dbHelper.deleteStudent(student, new OnModifyListener() {
                @Override
                public void onSuccess() {
                    Snackbar.make(binding.getRoot(), "Student deleted successfully", Snackbar.LENGTH_LONG).show();
                    students.remove(student);
                    StudentFragment std = (StudentFragment) fragment;
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.remove(std);
                    transaction.commit();
                    if (students.isEmpty()) binding.tvEmpty.setVisibility(View.VISIBLE);
                }

                        @Override
                        public void onAlreadyExists() {}

                @Override
                public void onFailure(Exception e) {
                    Snackbar.make(binding.getRoot(), "Failed to delete student: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.tvEmpty.setVisibility(View.GONE);
        loadAllStudents();
    }
}
