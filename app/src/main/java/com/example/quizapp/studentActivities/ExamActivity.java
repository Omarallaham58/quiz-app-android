package com.example.quizapp.studentActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quizapp.SplashActivity;
import com.example.quizapp.adapters.QuestionPagerAdapter;
import com.example.quizapp.classes.Enrollment;
import com.example.quizapp.listeners.AnswerStateListener;
import com.example.quizapp.classes.Course;
import com.example.quizapp.classes.Question;
import com.example.quizapp.databinding.ActivityExamBinding;
import com.example.quizapp.listeners.OnLoadedListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ExamActivity extends AppCompatActivity implements AnswerStateListener {//Checked

    private List<Question> questionList = new ArrayList<>();
    private Course course;
    private CountDownTimer countDownTimer;
    private long remainingMillis;
    public ActivityExamBinding binding;
    private Enrollment enrollment;
    Map<String,Set<String>> selectedOptions = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        course = (Course) getIntent().getSerializableExtra("course");
        enrollment = (Enrollment) getIntent().getSerializableExtra("enrollment");

        if (course == null || enrollment == null) {
           // Log.d("Exam", "NULL course or enrollment");
            finish();
            return;
        }

        enrollment.setLastExamEndDate(course.getEndDate());
        binding.tvCourseCode.setText("Course: " + course.getCourseCode());
        binding.tvPenaltyNotice.setVisibility(View.VISIBLE);

        SplashActivity.dbHelper.getAllQuestionsPerCourse(course, new OnLoadedListener<Question>() {
            @Override
            public void onLoaded(List<Question> items) {
                Collections.shuffle(items);
                questionList = items.size() > course.getNbQuestions() ? items.subList(0, course.getNbQuestions()) : items;
                setupExam();
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.viewPager, "Failed to load questions", Snackbar.LENGTH_LONG).show();
            }
        });

        binding.btnSubmit.setOnClickListener(v -> confirmSubmit());

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateQuestionIndicator(position);
            }
        });
    }



    private void setupExam() {
        QuestionPagerAdapter adapter = new QuestionPagerAdapter(this, questionList);
        binding.viewPager.setAdapter(adapter);


        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText("Q" + (position + 1))
        ).attach();

        updateQuestionIndicator(0);
        startTimer();
    }




    private void updateQuestionIndicator(int position) {
        binding.tvQuestionIndicator.setText("Question " + (position + 1) + " of " + questionList.size());
    }

    private void startTimer() {
        remainingMillis = course.getExamDuration() * 60 * 1000L;
        countDownTimer = new CountDownTimer(remainingMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                submitExam(false);
            }
        }.start();
    }

    private void updateTimerText() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % 60;
        binding.tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void confirmSubmit() {
        new AlertDialog.Builder(this)
                .setTitle("Submit Exam")
                .setMessage("Are you sure you want to submit your exam now?")
                .setPositiveButton("Yes", (dialog, which) -> submitExam(false))
                .setNegativeButton("No", null)
                .show();
    }



    private void submitExam(boolean goToDashboard) {
        if (countDownTimer != null) countDownTimer.cancel();
        double totalGrade = 0;
        double gradePerQuestion = 100.0 / questionList.size();

        int totalCorrectSelected = 0;
        int totalIncorrectSelected = 0;
        ArrayList<Integer> correctPerQuestion = new ArrayList<>();
        ArrayList<Integer> incorrectPerQuestion = new ArrayList<>();

       // for (int i = 0; i < questionList.size(); i++) {
        for(String tag : selectedOptions.keySet()){
//            StudentQuestionFragment fragment = (StudentQuestionFragment) getSupportFragmentManager().findFragmentByTag("f" + i);
          //  StudentQuestionFragment fragment = (StudentQuestionFragment) getSupportFragmentManager().findFragmentByTag(tag);

           // if (fragment == null) continue;
            int index = Integer.parseInt(tag.substring(1));
//            Question question = questionList.get(i);
            Question question = questionList.get(index);
            List<Integer> correctAnswers = question.getCorrectOptions();
//            Set<String> selectedAnswers = fragment.getSelectedOptions();
            Set<String> selectedAnswers = getOptionsByQuestionTag(tag);
            //if(selectedAnswers == null) continue;

            int totalCorrect = correctAnswers.size();
            double gradePerOption = gradePerQuestion / totalCorrect;
            double questionScore = 0;

            int questionCorrect = 0;
            int questionIncorrect = 0;

            //assert selectedAnswers != null;
            for (String ans : selectedAnswers) {
                int selected = Integer.parseInt(ans);
                if (correctAnswers.contains(selected)) {
                    questionScore += gradePerOption;
                    questionCorrect++;
                } else {
                    questionScore -= gradePerOption; // Penalty
                    questionIncorrect++;
                }
            }

            totalCorrectSelected += questionCorrect;
            totalIncorrectSelected += questionIncorrect;
            correctPerQuestion.add(questionCorrect);
            incorrectPerQuestion.add(questionIncorrect);
            totalGrade += Math.max(questionScore, 0);
        }

        enrollment.setGrade(totalGrade);
        enrollment.setPassed(totalGrade >= 50);
        enrollment.setLastExamEndDate(course.getEndDate());

        double finalTotalGrade = totalGrade;
        int finalTotalCorrectSelected = totalCorrectSelected;
        int finalTotalIncorrectSelected = totalIncorrectSelected;
        SplashActivity.dbHelper.updateEnrollment(enrollment, new OnLoadedListener<Boolean>() {
            @Override
            public void onLoaded(List<Boolean> result) {
                if (!goToDashboard) {
                    Intent intent = new Intent(ExamActivity.this, ExamResultActivity.class);
                    intent.putExtra("grade", finalTotalGrade + "");
                    intent.putExtra("correct", finalTotalCorrectSelected);
                    intent.putExtra("incorrect", finalTotalIncorrectSelected);
                    intent.putIntegerArrayListExtra("correctPerQuestion", correctPerQuestion);
                    intent.putIntegerArrayListExtra("incorrectPerQuestion", incorrectPerQuestion);
                    startActivity(intent);
                }
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Snackbar.make(binding.getRoot(), "Failed to update enrollment: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }








    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        //no call for super.onBackPressed() bcz it finishes the activity
        new AlertDialog.Builder(this)
                .setTitle("Exit Exam?")
                .setMessage("Exiting now will submit your current answers and end the exam. Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                   // if (countDownTimer != null) countDownTimer.cancel();
                    submitExam(true);
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public Set<String> getOptionsByQuestionTag(String tag) {
        //return Collections.emptySet();

        return selectedOptions.getOrDefault(tag, new HashSet<String>());
    }

    @Override
    public void setAnswers(String tag, Set<String> answers) {

       // selectedOptions.put(tag,answers); synchronization issue might occur on answers

        selectedOptions.put(tag, new HashSet<>(answers));

    }
}
