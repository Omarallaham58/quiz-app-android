package com.example.quizapp.listeners;

import java.util.List;

public interface OnLoadedListener<T> {//checked
    public void onLoaded(List<T> items);
    void onFailure(Exception e);
}