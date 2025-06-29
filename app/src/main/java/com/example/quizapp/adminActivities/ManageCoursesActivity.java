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
import com.example.quizapp.adminFragments.CourseFragment;
import com.example.quizapp.classes.Course;
import com.example.quizapp.databinding.ActivityManageCoursesBinding;
import com.example.quizapp.listeners.OnEnrolledCheckListener;
import com.example.quizapp.listeners.OnLoadedListener;
import com.example.quizapp.listeners.OnModifyListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ManageCoursesActivity extends BaseAdminActivity {//Checked

    private FragmentManager fragmentManager;
    private List<Course> courses;
    private ActivityManageCoursesBinding binding;
    private String TAG_PREFIX = "frag_";
    private int nbLines = 0;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageCoursesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(R.id.toolbar);
        fragmentManager = getSupportFragmentManager();
        binding.btnAddCourse.setOnClickListener(v -> switchToAddCourseActivity());
    }

    private void switchToAddCourseActivity() {
        Intent intent = new Intent(this, AddCourseActivity.class);
        startActivity(intent);
    }

    public void loadAllCourses() {
        binding.progressLoader.setVisibility(View.VISIBLE);

        // Clear previous fragments
        List<Fragment> existingFragments = fragmentManager.getFragments();
        FragmentTransaction clearTransaction = fragmentManager.beginTransaction();
        for (Fragment frag : existingFragments) {
            if (frag != null && frag.getTag() != null && frag.getTag().startsWith(TAG_PREFIX)) {
                clearTransaction.remove(frag);
            }
        }
        clearTransaction.commitNow(); // Commit immediately to avoid overlaps

        nbLines = 0;

        SplashActivity.dbHelper.getAllCourses(new OnLoadedListener<Course>() {
            @Override
            public void onLoaded(List<Course> listCourses) {
                courses = listCourses;
                if(courses == null) binding.tvEmpty.setVisibility(View.VISIBLE);
                else{
                    //Log.d("COURSES", courses + "");
                    for(Course c : courses){
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        CourseFragment fragment = new CourseFragment(c, context);
                        fragment.registerDeleteListener(tag -> deleteFragment(tag, c));
                        String tag1 = TAG_PREFIX + nbLines++;
                        fragmentTransaction.add(R.id.frag_container, fragment, tag1);
                        fragmentTransaction.commit();
                    }
                }
                binding.progressLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(),e.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void deleteFragment(String tag, Course course) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if(fragment!=null){
            SplashActivity.dbHelper.hasEnrolledStudents(course, new OnEnrolledCheckListener() {
                @Override
                public void onCheck(boolean hasEnrolled) {
                    if (hasEnrolled) {
                        Snackbar.make(binding.getRoot(), "Cannot delete: students are enrolled", Snackbar.LENGTH_LONG).show();
                    } else {
                        SplashActivity.dbHelper.deleteCourse(course, new OnModifyListener() {
                            @Override
                            public void onSuccess() {
                                Snackbar.make(binding.getRoot(), "Course deleted successfully", Snackbar.LENGTH_LONG).show();
                                courses.remove(course);
                                CourseFragment row = (CourseFragment) fragment;
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.remove(row);
                                transaction.commit();
                                if(courses.isEmpty()) binding.tvEmpty.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAlreadyExists() {}

                            @Override
                            public void onFailure(Exception e) {
                                Snackbar.make(binding.getRoot(), "Deletion failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    Snackbar.make(binding.getRoot(), "Error checking enrollment: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.tvEmpty.setVisibility(View.GONE);
        loadAllCourses();
    }
}

