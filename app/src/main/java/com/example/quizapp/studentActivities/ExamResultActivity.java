package com.example.quizapp.studentActivities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapp.R;
import com.example.quizapp.databinding.ActivityExamResultBinding;

import java.util.ArrayList;

public class ExamResultActivity extends AppCompatActivity {//Checked

    ActivityExamResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExamResultBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String grade = (String)getIntent().getSerializableExtra("grade");
        int correct = getIntent().getIntExtra("correct", 0);
        int incorrect = getIntent().getIntExtra("incorrect", 0);
        ArrayList<Integer> correctPerQuestion = getIntent().getIntegerArrayListExtra("correctPerQuestion");
        ArrayList<Integer> incorrectPerQuestion = getIntent().getIntegerArrayListExtra("incorrectPerQuestion");



        binding.gradeIndicator.setValue(Float.parseFloat(grade));
        binding.pchOptions.setAnswerCounts(correct, incorrect);

    }
}