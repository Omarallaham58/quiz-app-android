package com.example.quizapp.adminActivities;

import android.os.Bundle;
import android.widget.CheckBox;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.R;
import com.example.quizapp.classes.Course;
import com.example.quizapp.classes.Question;
import com.example.quizapp.databinding.ActivityAddQuestionBinding;
import com.example.quizapp.listeners.OnModifyListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddQuestionActivity extends BaseAdminActivity {//Checked

    private ActivityAddQuestionBinding binding;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(R.id.toolbar);
        course = (Course) getIntent().getSerializableExtra("course");
        binding.btAdd.setOnClickListener((v)->addQuestion());
    }

    private void addQuestion() {
        boolean isValid = true;

        String q = binding.etText.getText().toString().trim();
        String op1 = binding.etOp1.getText().toString().trim();
        String op2 = binding.etOp2.getText().toString().trim();
        String op3 = binding.etOp3.getText().toString().trim();
        String op4 = binding.etOp4.getText().toString().trim();

        CheckBox cbOp1 = binding.cbOp1;
        CheckBox cbOp2 = binding.cbOp2;
        CheckBox cbOp3 = binding.cbOp3;
        CheckBox cbOp4 = binding.cbOp4;

        if(q.isEmpty()){
            binding.etText.setError("Required");
            isValid = false;
        }
        if(op1.isEmpty()){
            binding.etOp1.setError("Required");
            isValid = false;
        }
        if(op2.isEmpty()){
            binding.etOp2.setError("Required");
            isValid = false;
        }
        if(op3.isEmpty()){
            binding.etOp3.setError("Required");
            isValid = false;
        }
        if(op4.isEmpty()){
            binding.etOp4.setError("Required");
            isValid = false;
        }

        if (!cbOp1.isChecked() && !cbOp2.isChecked() && !cbOp3.isChecked() && !cbOp4.isChecked()) {
            Snackbar.make(binding.getRoot(),"Please check at least one correct answer!",Snackbar.LENGTH_LONG).show();
            isValid = false;
        }

        if (!isValid) return;
        HashMap<String, String> options = new HashMap<>();
        options.put("1", op1);
        options.put("2", op2);
        options.put("3", op3);
        options.put("4", op4);

        List<Integer> correctOptions = new ArrayList<>();
        if(cbOp1.isChecked()) correctOptions.add(1);
        if(cbOp2.isChecked()) correctOptions.add(2);
        if(cbOp3.isChecked()) correctOptions.add(3);
        if(cbOp4.isChecked()) correctOptions.add(4);

        Question question = new Question();
        question.setQuestionText(q);
        question.setCourse(course);
        question.setOptions(options);
        question.setCorrectOptions(correctOptions);

        SplashActivity.dbHelper.addQuestion(question, new OnModifyListener() {
            @Override
            public void onSuccess() {
                Snackbar.make(binding.getRoot(),"Question added successfully",Snackbar.LENGTH_LONG).show();
                binding.etText.setText("");
                binding.etOp1.setText("");
                binding.etOp2.setText("");
                binding.etOp3.setText("");
                binding.etOp4.setText("");

                binding.cbOp1.setChecked(true);
                binding.cbOp2.setChecked(false);
                binding.cbOp3.setChecked(false);
                binding.cbOp4.setChecked(false);
                binding.etText.requestFocus();
            }

            @Override
            public void onAlreadyExists() {
                Snackbar.make(binding.getRoot(),"Question already exists",Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(),"Error: " + e.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }
}