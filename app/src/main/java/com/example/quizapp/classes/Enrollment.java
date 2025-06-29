package com.example.quizapp.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Enrollment implements Serializable {//Checked
    private Course course;
    private Student student;
    private double grade;
    private boolean passed;
    private Date lastExamEndDate;

    public Enrollment(Course course, Student student, double grade, boolean passed) {
        this.course = course;
        this.student = student;
        this.grade = grade;
        this.passed = passed;
    }

    public Enrollment() {
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public Date getLastExamEndDate() {
        return lastExamEndDate;
    }

    public void setLastExamEndDate(Date lastExamEndDate) {
        this.lastExamEndDate = lastExamEndDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "Enrollment{" +
                "course=" + course +
                ", student=" + student +
                ", grade=" + grade +
                ", passed=" + passed +
                ", laseExamEndDate=" + lastExamEndDate +
                '}';
    }
}
