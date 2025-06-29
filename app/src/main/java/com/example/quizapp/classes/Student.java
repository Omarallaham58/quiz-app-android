package com.example.quizapp.classes;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

public class Student implements Serializable {//Checked
    private int fileNumber;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private double gpa;
    private java.util.Date dob;
    private String email;

    @Exclude
    private List<Enrollment> enrollments;

    //constructor for fetching from db
    public Student(int fileNumber, String firstName, String lastName, String username, String password, double gpa, Date dob, String email) {
        this.fileNumber = fileNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.gpa = gpa;
        this.dob = dob;
        this.email = email;
    }

    //creating the student for first time 1st time
    public Student(String firstName, String lastName,  Date dob, String email){
        
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
        this.gpa = 0;
       // username = createUsername();
       // password = createPassword();
        


    }




    public void generateCredentials(long lastId) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(dob);

        char[] usernameArray = {
                firstName.charAt(0), lastName.charAt(0),
                formattedDate.charAt(8), formattedDate.charAt(9),
                formattedDate.charAt(5), formattedDate.charAt(6),
                formattedDate.charAt(0), formattedDate.charAt(1),
                formattedDate.charAt(2), formattedDate.charAt(3)
        };
        this.username = new String(usernameArray) + lastId;

        Random rand = new Random();
        int k1 = rand.nextInt(firstName.length());
        int k2 = rand.nextInt(lastName.length());
        int k3 = rand.nextInt(("" + hashCode()).length());

        char[] passwdArray = {
                firstName.charAt(k1), lastName.charAt(k2),
                formattedDate.charAt(8), formattedDate.charAt(9),
                formattedDate.charAt(5), formattedDate.charAt(6),
                formattedDate.charAt(0), formattedDate.charAt(1),
                formattedDate.charAt(2), formattedDate.charAt(3)
        };

        this.password = Constants.shuffle(new String(passwdArray) + ("" + hashCode()).charAt(k3));
    }



    public Student() {
    }

    public int getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(int fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    @Exclude
    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    @Exclude
    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public java.util.Date getDob() {
        return dob;
    }

    public void setDob(java.util.Date dob) {
        this.dob = dob;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @NonNull
    @Override
    public String toString() {
        return "Student{" +
                "fileNumber=" + fileNumber +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", gpa=" + gpa +
                ", dob=" + dob +
                ", email='" + email + '\'' +
                '}';
    }


}

