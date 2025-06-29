package com.example.quizapp.classes;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Course  implements Serializable {//Checked
    private String courseCode;
    private String courseName;
    private int credits;
    private Date startDate;
    private Date endDate;
    private int examDuration; // in minutes
    private int nbQuestions;

    @Exclude
    private List<Question> questions;

    public Course(String courseCode, String courseName, int credits, Date startDate, Date endDate, int examDuration, int nbQuestions) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.startDate = startDate;
        this.endDate = endDate;
        this.examDuration = examDuration;
        this.nbQuestions = nbQuestions;
    }

    public Course() {
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getExamDuration() {
        return examDuration;
    }

    public void setExamDuration(int examDuration) {
        this.examDuration = examDuration;
    }

    public int getNbQuestions() {
        return nbQuestions;
    }

    public void setNbQuestions(int nbQuestions) {
        this.nbQuestions = nbQuestions;
    }

    @Exclude
    public List<Question> getQuestions() {
        return questions;
    }

    @Exclude
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credits=" + credits +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", examDuration=" + examDuration +
                ", nbQuestions=" + nbQuestions +
                ", questions=" + questions +
                '}';
    }
}
