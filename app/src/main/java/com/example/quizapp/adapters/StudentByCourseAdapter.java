package com.example.quizapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.quizapp.R;
import com.example.quizapp.classes.Enrollment;
import com.example.quizapp.databinding.StudentItemListBinding;

import java.util.List;

public class StudentByCourseAdapter extends ArrayAdapter<Enrollment> {//Checked

    private List<Enrollment> enrollments;
    private StudentItemListBinding binding;
    public StudentByCourseAdapter(Context context, List<Enrollment> enrollments) {
        super(context, android.R.layout.simple_list_item_1, enrollments);
        this.enrollments = enrollments;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent)
    {
        Enrollment enrollment = enrollments.get(position);
        if (row==null) {
            LayoutInflater inflater = ((Activity) super.getContext()).getLayoutInflater();
            row = inflater.inflate(R.layout.student_item_list, parent, false);
            binding = StudentItemListBinding.bind(row);
        }
        binding.fileNumber.setText(enrollment.getStudent().getFileNumber()+"");
        binding.firstName.setText(enrollment.getStudent().getFirstName());
        binding.lastName.setText(enrollment.getStudent().getLastName());
        if(enrollment.getGrade()<0) binding.grade.setText("N/A");
        else binding.grade.setText(enrollment.getGrade()+"");
        binding.passed.setText(enrollment.isPassed()? "Yes" : "No");
        binding.passed.setTextColor(enrollment.isPassed()? Color.GREEN : Color.RED);

        return row;
    }


}
