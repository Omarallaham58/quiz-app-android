package com.example.quizapp.adminFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.classes.Course;
import com.example.quizapp.classes.Enrollment;
import com.example.quizapp.classes.Student;
import com.example.quizapp.databinding.FragmentAssignStudentBinding;
import com.example.quizapp.listeners.OnModifyListener;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AssignStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssignStudentFragment extends Fragment {//Checked

    private CheckBox cb;
    private FragmentAssignStudentBinding binding;

    private Student student;
    private Course course;

    public AssignStudentFragment() {
        // Required empty public constructor
    }

    public AssignStudentFragment(Student student, Course course) {
        this.student = student;
        this.course = course;
    }

    public static AssignStudentFragment newInstance() {
        AssignStudentFragment fragment = new AssignStudentFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_assign_student, container, false);
        binding = FragmentAssignStudentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fileNumber.setText(student.getFileNumber()+"");
        binding.firstName.setText(student.getFirstName());
        binding.lastName.setText(student.getLastName());
        cb = binding.cbAssign;
        for(Enrollment enrollment : student.getEnrollments()){
            if(enrollment.getCourse().getCourseCode().equals(course.getCourseCode())){
                cb.setChecked(true);
                if(enrollment.getGrade()>=0)
                    cb.setEnabled(false);
            }
        }
        cb.setOnCheckedChangeListener((v,isChecked)->updateStudentAssignment(isChecked));
    }

    private void updateStudentAssignment(boolean isChecked) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setGrade(-1);
        enrollment.setPassed(false);
        SplashActivity.dbHelper.updateAssignment(enrollment, isChecked, new OnModifyListener() {
            @Override
            public void onSuccess() {
                Snackbar.make(binding.getRoot(), isChecked ? "Student assigned" : "Student unassigned",Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onAlreadyExists() {

            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(), "Error: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}