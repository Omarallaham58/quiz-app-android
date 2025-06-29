package com.example.quizapp.adminActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.R;
import com.example.quizapp.classes.Course;
import com.example.quizapp.databinding.ActivityCourseDetailsBinding;
import com.example.quizapp.listeners.OnEnrolledCheckListener;
import com.example.quizapp.listeners.OnLoadedListener;
import com.example.quizapp.listeners.OnModifyListener;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CourseDetailsActivity extends BaseAdminActivity {//Checked

    private ActivityCourseDetailsBinding binding;
    private Course course;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(R.id.toolbar);
        course = (Course) getIntent().getSerializableExtra("course");
        binding.btnViewQuestions.setOnClickListener((v)->switchToManageQuestions(this.course));
        binding.btnViewStudents.setOnClickListener(v->switchToViewStudents(this.course));
        binding.btnAssignStudents.setOnClickListener(v->switchToAssignStudents(this.course));
        binding.btnUpdateCourse.setOnClickListener(v->switchToUpdateCourse(this.course));
        binding.btnDeleteCourse.setOnClickListener(v->deleteCourse(this.course));
        binding.btnVisualize.setOnClickListener(v->switchToCourseHistogram(this.course));
//        loadCourseInfo();
    }

    private void switchToCourseHistogram(Course course) {
        Intent intent = new Intent(this, CourseHistogramActivity.class);
        intent.putExtra("course", course);
        startActivity(intent);
        //finish();
    }

    private void switchToAssignStudents(Course course) {
        Intent intent = new Intent(this, AssignStudentToCourse.class);
        intent.putExtra("course", course);
        startActivity(intent);
    }

    private void deleteCourse(Course course) {
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
                            Intent intent = new Intent(context, ManageCoursesActivity.class);
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
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(), "Error checking enrollment: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void switchToUpdateCourse(Course course) {
        Intent intent = new Intent(this, UpdateCourseActivity.class);
        intent.putExtra("course", course);
        startActivity(intent);
    }

    private void switchToViewStudents(Course course) {
        Intent intent = new Intent(this, ViewStudentsByCourseActivity.class);
        intent.putExtra("course", course);
        startActivity(intent);
    }

    private void switchToManageQuestions(Course course) {
        Intent intent = new Intent(this, ManageQuestionsActivity.class);
        intent.putExtra("course", course);
        startActivity(intent);
    }

    private void loadCourseInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        binding.tvCourseCode.setText(course.getCourseCode());
        binding.tvCourseName.setText(course.getCourseName());
        binding.tvCredits.setText(String.valueOf(course.getCredits()));
        binding.tvDuration.setText(String.valueOf(course.getExamDuration()));
        binding.tvNbQuestions.setText(String.valueOf(course.getNbQuestions()));
        binding.tvStartDate.setText(sdf.format(course.getStartDate()));
        binding.tvEndDate.setText(sdf.format(course.getEndDate()));
        binding.progressLoader.setVisibility(View.GONE);
        binding.container.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.container.setVisibility(View.GONE);
        binding.progressLoader.setVisibility(View.VISIBLE);
        SplashActivity.dbHelper.getCourseByCode(course.getCourseCode(), new OnLoadedListener<Course>() {
            @Override
            public void onLoaded(List<Course> items) {
                if(!items.isEmpty()){
                    course = items.get(0);
                    loadCourseInfo();
                }

            }
            @Override
            public void onFailure(Exception e) {

                Snackbar.make(binding.getRoot(),"Database Error: " + e.getMessage(),
                        Snackbar.LENGTH_LONG).show();
            }
        });

      //  loadCourseInfo();

    }
}