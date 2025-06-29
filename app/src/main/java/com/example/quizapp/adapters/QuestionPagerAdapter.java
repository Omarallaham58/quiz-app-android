package com.example.quizapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quizapp.classes.Question;
import com.example.quizapp.studentFragments.StudentQuestionFragment;

import java.util.List;

public class QuestionPagerAdapter extends FragmentStateAdapter {//Checked

    private List<Question> questions;

    public QuestionPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Question> questions) {
        super(fragmentActivity);
        this.questions = questions;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return StudentQuestionFragment.newInstance(questions.get(position));
    }

    @Override
    public int getItemCount() {

        return questions.size();
    }
}
