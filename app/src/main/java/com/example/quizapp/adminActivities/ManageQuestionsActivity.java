package com.example.quizapp.adminActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.R;
import com.example.quizapp.adminFragments.QuestionFragment;
import com.example.quizapp.classes.Course;
import com.example.quizapp.classes.Question;
import com.example.quizapp.databinding.ActivityManageQuestionsBinding;
import com.example.quizapp.listeners.OnLoadedListener;
import com.example.quizapp.listeners.OnModifyListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ManageQuestionsActivity extends BaseAdminActivity {//Checked

    private FragmentManager fragmentManager;
    private Course course;
    private List<Question> questions;
    private ActivityManageQuestionsBinding binding;
    private String TAG_PREFIX = "frag_";
    private int nbLines = 0;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(R.id.toolbar);
        fragmentManager = getSupportFragmentManager();
        course = (Course) getIntent().getSerializableExtra("course");
        binding.btnAddQuestion.setOnClickListener(v->switchToAddQuestion());
    }

    private void switchToAddQuestion() {
        Intent intent = new Intent(this, AddQuestionActivity.class);
        intent.putExtra("course", course);
        startActivity(intent);
    }

    private void loadQuestions() {
        binding.progressLoader.setVisibility(View.VISIBLE);

        // Clear previous fragments
        List<Fragment> existingFragments = fragmentManager.getFragments();
        FragmentTransaction clearTransaction = fragmentManager.beginTransaction();
        for (Fragment frag : existingFragments) {
            if (frag != null && frag.getTag() != null && frag.getTag().startsWith(TAG_PREFIX)) {
                clearTransaction.remove(frag);
            }
        }
        clearTransaction.commitNow(); // Commit immediately to avoid overlaps

        nbLines = 0;

        SplashActivity.dbHelper.getAllQuestionsPerCourse(course, new OnLoadedListener<Question>() {
            @Override
            public void onLoaded(List<Question> items) {
                questions = items;
                if(questions.isEmpty()) binding.tvEmpty.setVisibility(View.VISIBLE);
                else{
                    //Log.d("COURSES", questions + "");
                    for(Question q : questions){
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        QuestionFragment fragment = new QuestionFragment(q, context);
                        fragment.registerDeleteListener(tag -> deleteFragment(tag, q));
                        String tag1 = TAG_PREFIX + nbLines++;
                        fragmentTransaction.add(R.id.frag_container, fragment, tag1);
                        fragmentTransaction.commit();
                    }
                }
                binding.progressLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(),e.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void deleteFragment(String tag, Question question) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if(fragment!=null){
            SplashActivity.dbHelper.deleteQuestion(question.getQuestionId(), new OnModifyListener() {
                @Override
                public void onSuccess() {
                    questions.remove(question);
                    QuestionFragment row = (QuestionFragment) fragment;
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.remove(row);
                    transaction.commit();
                    if(questions.isEmpty()) binding.tvEmpty.setVisibility(View.VISIBLE);
                    Snackbar.make(binding.getRoot(),"Question deleted successfully",Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onAlreadyExists() {

                }

                @Override
                public void onFailure(Exception e) {
                    Snackbar.make(binding.getRoot(),e.getMessage(),Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.tvEmpty.setVisibility(View.GONE);
        loadQuestions();
    }
}