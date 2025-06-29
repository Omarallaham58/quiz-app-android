package com.example.quizapp.studentFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//import com.example.quizapp.studentActivities.ExamActivity;
import com.example.quizapp.SplashActivity;
import com.example.quizapp.studentActivities.StudentCourseDetailsActivity;
import com.example.quizapp.classes.Course;
import com.example.quizapp.classes.Enrollment;
import com.example.quizapp.classes.Question;
import com.example.quizapp.studentActivities.ExamActivity;
import com.example.quizapp.databinding.FragmentStudentCourseBinding;
import com.example.quizapp.listeners.OnLoadedListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentCourseFragment extends Fragment {//Checked

    private Course course;
    private Enrollment enrollment;
    public FragmentStudentCourseBinding binding;

    public StudentCourseFragment(Course course, Enrollment enrollment) {
        this.course = course;
        this.enrollment = enrollment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentStudentCourseBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvCourseCode.setText(course.getCourseCode());
        binding.tvCredits.setText(String.valueOf(course.getCredits()));

        if (enrollment.getGrade() >= 0) {
            String result = String.format(Locale.getDefault(), "%.0f (%s)",
                    enrollment.getGrade(),
                    enrollment.isPassed() ? "Passed" : "Failed");
            binding.tvGrade.setText(result);
        } else {
            binding.tvGrade.setText("N/A");
        }

        Date now = new Date();
        Date currentEndDate = course.getEndDate();
        Date lastAttemptedEndDate = enrollment.getLastExamEndDate();
        //Log.d("Enrollment","Before testing: " + lastAttemptedEndDate);
        // Logic to control exam button visibility
        if (enrollment.isPassed()) {
            // Already passed: hide the button
           // Log.d("Enrollment","Passed cond");
            binding.btnTakeExam.setVisibility(View.INVISIBLE);

        } else if (enrollment.getGrade() >= 0 && lastAttemptedEndDate != null &&
                currentEndDate.compareTo(lastAttemptedEndDate) == 0) {
            //Log.d("Enrollment", "Entered cond of date: " + lastAttemptedEndDate);
            // Failed before & still the same exam session => not eligible yet
            binding.btnTakeExam.setVisibility(View.INVISIBLE);

        } else if (now.before(course.getStartDate())) {
            // Exam hasn't started yet
           // Log.d("Enrollment","Before start date cond");
            binding.btnTakeExam.setVisibility(View.VISIBLE);

            binding.btnTakeExam.setEnabled(true);
            binding.btnTakeExam.setBackgroundColor(Color.GRAY);
            binding.btnTakeExam.setTextColor(Color.WHITE);
            binding.btnTakeExam.setOnClickListener(v -> {

                Snackbar.make(view,"Exam's start date is on "+course.getStartDate(),Snackbar.LENGTH_LONG).show();

            });
            //binding.btnTakeExam.setHint("Exam start date is at " + course.getStartDate().toString());
            //Snackbar.make(view,"test",Snackbar.LENGTH_LONG).show();
        } else if (now.after(course.getEndDate())) {
            // Exam expired and not taken: hide the button
            Log.d("Enrollment", "After end date cond");
            binding.btnTakeExam.setVisibility(View.INVISIBLE);
        }

        else {
            // Check if the course has questions before allowing to take the exam
            SplashActivity.dbHelper.getAllQuestionsPerCourse(course, new OnLoadedListener<Question>() {
                @Override
                public void onLoaded(List<Question> questions) {
                    if (questions.isEmpty()) {
                       // Log.d("Enrollment", "Course has no questions");
                        binding.btnTakeExam.setVisibility(View.VISIBLE);
                        binding.btnTakeExam.setEnabled(true);
                        binding.btnTakeExam.setBackgroundColor(Color.GRAY);
                        binding.btnTakeExam.setTextColor(Color.WHITE);
                        binding.btnTakeExam.setOnClickListener(v ->
                                Snackbar.make(view, "Error: No questions were found for " +
                                        course.getCourseCode() +". Contact the admin and report the " +
                                        "issue.", Snackbar.LENGTH_LONG).show());
                    } else {
                       // Log.d("Enrollment", "Valid cond - has questions");
                        binding.btnTakeExam.setVisibility(View.VISIBLE);
                        binding.btnTakeExam.setEnabled(true);
                        binding.btnTakeExam.setOnClickListener(v -> {
                            Intent intent = new Intent(requireContext(), ExamActivity.class);
                            enrollment.setLastExamEndDate(course.getEndDate());
                            intent.putExtra("course", course);
                            intent.putExtra("enrollment", enrollment);
                            startActivity(intent);
                        });
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Snackbar.make(view, "Failed to load questions: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        }


        binding.btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), StudentCourseDetailsActivity.class);
            intent.putExtra("course", course);
            startActivity(intent);
        });
    }


}
