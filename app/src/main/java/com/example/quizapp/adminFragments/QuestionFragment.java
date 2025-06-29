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
import com.example.quizapp.adminActivities.UpdateQuestionActivity;
import com.example.quizapp.classes.Course;
import com.example.quizapp.classes.Question;
import com.example.quizapp.databinding.FragmentQuestionBinding;
import com.example.quizapp.listeners.DeleteFragListener;

import java.util.List;


public class QuestionFragment extends Fragment {//Checked

    public DeleteFragListener listener;
    public FragmentQuestionBinding binding;
    public Course course;
    public Question question;
    public Context context;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public QuestionFragment(Question question, Context context) {
        this.context = context;
        this.question = question;
    }

    public static QuestionFragment newInstance() {
        QuestionFragment fragment = new QuestionFragment();
        return fragment;
    }

    public void registerDeleteListener(DeleteFragListener listener){
        this.listener = listener;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentQuestionBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.iconOption1.setVisibility(View.GONE);
        binding.iconOption2.setVisibility(View.GONE);
        binding.iconOption3.setVisibility(View.GONE);
        binding.iconOption4.setVisibility(View.GONE);

        binding.tvQuestion.setText(question.getQuestionText());
        binding.tvOption1.setText("a) " + question.getOptions().get("1"));
        binding.tvOption2.setText("b) " + question.getOptions().get("2"));
        binding.tvOption3.setText("c) " + question.getOptions().get("3"));
        binding.tvOption4.setText("d) " + question.getOptions().get("4"));

        List<Integer> correctOptions = question.getCorrectOptions();
        for (Integer index : correctOptions) {
            switch (index) {
                case 1: binding.iconOption1.setVisibility(View.VISIBLE); break;
                case 2: binding.iconOption2.setVisibility(View.VISIBLE); break;
                case 3: binding.iconOption3.setVisibility(View.VISIBLE); break;
                case 4: binding.iconOption4.setVisibility(View.VISIBLE); break;
            }
        }
        binding.btnUpdate.setOnClickListener((v)->switchToUpdate(question));
        binding.btnDelete.setOnClickListener((v)->notifyDeleteListener());
    }

    private void switchToUpdate(Question question) {
        Intent intent = new Intent(context, UpdateQuestionActivity.class);
        intent.putExtra("question", question);
        startActivity(intent);
    }

    private void notifyDeleteListener() {
        listener.notifyDelete(getTag());
    }
}