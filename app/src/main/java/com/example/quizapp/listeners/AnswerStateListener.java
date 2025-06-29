package com.example.quizapp.listeners;

import java.util.Set;

public interface AnswerStateListener {//Checked

    public Set<String> getOptionsByQuestionTag(String tag);

    public void setAnswers(String tag, Set<String> answers);

}
