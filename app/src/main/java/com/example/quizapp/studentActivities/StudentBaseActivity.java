package com.example.quizapp.studentActivities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quizapp.LoginActivity;
import com.example.quizapp.R;
import com.example.quizapp.SessionManager;

public abstract class StudentBaseActivity extends AppCompatActivity {//Checked



    protected void setupToolbar(int toolbarId, String name) {
        Toolbar toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(name);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SessionManager.logout(this);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
          // finish();
          //  return true;
        }
        return true;//super.onOptionsItemSelected(item);
    }
}