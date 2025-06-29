package com.example.quizapp.classes;

import java.util.Random;

public class Constants {//Checked
    public static final String STUDENT = "Student";
    public static final String COURSE = "Course";
    public static final String QUESTION = "Question";
    public static final String ENROLLMENT = "Enrollment";
    public static final String COUNTERS = "Counters";
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin123";



    public static String shuffle(String str) {
        char[] array = str.toCharArray();
        Random rng = new Random();
        int n = array.length;

        while (n > 1) {
            n--;
            int k = rng.nextInt(n);
            char value = array[k];
            array[k] = array[n];
            array[n] = value;
        }

        return new String(array);
    }
}
