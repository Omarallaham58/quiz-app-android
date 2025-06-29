package com.example.quizapp.classes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Question implements Serializable {//Checked
    private int questionId;
    private String questionText;
    private List<Integer> correctOptions;
    private HashMap<String, String> options;
    private Course course;

    public Question(int questionId, String questionText, List<Integer> correctOptions, HashMap<String, String> options, Course course) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.correctOptions = correctOptions;
        this.options = options;
        this.course = course;
    }

    public Question() {
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<Integer> getCorrectOptions() {
        return correctOptions;
    }

    public void setCorrectOptions(List<Integer> correctOptions) {
        this.correctOptions = correctOptions;
    }

    public HashMap<String, String> getOptions() {
        return options;
    }

    public void setOptions(HashMap<String, String> options) {
        this.options = options;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", questionText='" + questionText + '\'' +
                ", correctOptions=" + correctOptions +
                ", options=" + options +
                ", course=" + course +
                '}';
    }
}
