package com.example.quizapp.adminFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizapp.R;
import com.example.quizapp.adminActivities.CourseDetailsActivity;
import com.example.quizapp.adminActivities.UpdateCourseActivity;
import com.example.quizapp.classes.Course;
import com.example.quizapp.databinding.FragmentCourseBinding;
import com.example.quizapp.listeners.DeleteFragListener;


public class CourseFragment extends Fragment {//Checked

    public DeleteFragListener listener;
    public FragmentCourseBinding binding;
    public Course course;
    public Context context;

    public CourseFragment() {
        // Required empty public constructor
    }

    public CourseFragment(Course course, Context context) {
        this.course = course;
        this.context = context;
    }

    public static CourseFragment newInstance() {
        CourseFragment fragment = new CourseFragment();
        return fragment;
    }

    public void registerDeleteListener(DeleteFragListener listener){
        this.listener = listener;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_course, container, false);
        binding = FragmentCourseBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvCourseCode.setText(course.getCourseCode());
        binding.tvCourseName.setText(course.getCourseName());
        binding.btnDetails.setOnClickListener(e->openDetails(course));
        binding.btnUpdate.setOnClickListener(v->switchToUpdate(course));
        binding.btnDelete.setOnClickListener(v->notifyDeleteListener());
    }

    private void notifyDeleteListener() {
        listener.notifyDelete(getTag());
    }

    private void switchToUpdate(Course course) {
        Intent intent = new Intent(context, UpdateCourseActivity.class);
        intent.putExtra("course", course);
        startActivity(intent);
    }

    private void openDetails(Course course) {
        Intent intent = new Intent(context, CourseDetailsActivity.class);
        intent.putExtra("course", course);
        startActivity(intent);

    }

}