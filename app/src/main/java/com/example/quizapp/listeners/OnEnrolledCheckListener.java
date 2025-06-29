package com.example.quizapp.listeners;

public interface OnEnrolledCheckListener {//Checked
    void onCheck(boolean hasEnrolled);
    void onFailure(Exception e);
}