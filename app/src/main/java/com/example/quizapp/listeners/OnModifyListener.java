package com.example.quizapp.listeners;

public interface OnModifyListener {//Checked
    void onSuccess();
    void onAlreadyExists();
    void onFailure(Exception e);
   default void onAlreadyExists(String msg){}
}