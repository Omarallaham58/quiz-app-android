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
import com.example.quizapp.adminActivities.StudentDetailsActivity;
import com.example.quizapp.adminActivities.UpdateStudentActivity;
import com.example.quizapp.classes.Student;
import com.example.quizapp.databinding.FragmentStudentBinding;
import com.example.quizapp.listeners.DeleteFragListener;

public class StudentFragment extends Fragment {//Checked

    public DeleteFragListener listener;
    public FragmentStudentBinding binding;
    public Student student;
    public Context context;

    public StudentFragment() {}

    public StudentFragment(Student student, Context context) {
        this.student = student;
        this.context = context;
    }

    public static StudentFragment newInstance() {
        return new StudentFragment();
    }

    public void registerDeleteListener(DeleteFragListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStudentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvFileNumber.setText(String.valueOf(student.getFileNumber()));
        binding.tvFullName.setText(student.getFirstName() + " " + student.getLastName());
        binding.btnUpdate.setOnClickListener(v -> switchToUpdate(student));
        binding.btnDelete.setOnClickListener(v -> notifyDeleteListener());
        binding.btnDetails.setOnClickListener(v -> openDetails(student));
    }

    private void notifyDeleteListener() {
        if (listener != null) {
            listener.notifyDelete(getTag());
        }
    }

    private void switchToUpdate(Student student) {
        Intent intent = new Intent(context, UpdateStudentActivity.class);
         intent.putExtra("student", student);
        startActivity(intent);
    }

    private void openDetails(Student student) {
        Intent intent = new Intent(context, StudentDetailsActivity.class);
        intent.putExtra("student", student);
        startActivity(intent);
    }
}
