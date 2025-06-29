

package com.example.quizapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.adminActivities.ManageStudentsActivity;
import com.example.quizapp.classes.Student;
import com.example.quizapp.listeners.OnLoadedListener;
import com.example.quizapp.studentActivities.StudentActivity;

import java.util.List;

//import com.example.quizapp.AdminActivity;
//import com.example.quizapp.studentActivities.StudentActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {//checked

    private static final int SPLASH_TIME = 3000; // 3 seconds
    public static DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        dbHelper = new DBHelper(); // not affected by finish() since it's static to the class not to activity

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (SessionManager.isLoggedIn(this)) {
                if (SessionManager.isAdmin(this)) {
                    // Logged in as admin
                    startActivity(new Intent(this, ManageStudentsActivity.class));
                    finish();
                } else {
                    // Logged in as student
                    String username = SessionManager.getUsername(this);
                    if (username == null) {
                        SessionManager.logout(this);
                        redirectToLogin(); // fallback
                        finish();
                        return;
                    }


                    dbHelper.getStudentByUsername(username, new OnLoadedListener<Student>() {

                        @Override
                        public void onLoaded(List<Student> students) {
                            if (students.isEmpty()) {
                                SessionManager.logout(SplashActivity.this);
                                redirectToLogin();
                                finish();
                            } else {
                                Intent intent = new Intent(SplashActivity.this, StudentActivity.class);
                                intent.putExtra("student", students.get(0));
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {

                            redirectToLogin();
                            finish();
                        }
                    });



                }
            } else {
                redirectToLogin();
                finish();
            }

        }, SPLASH_TIME);
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}

