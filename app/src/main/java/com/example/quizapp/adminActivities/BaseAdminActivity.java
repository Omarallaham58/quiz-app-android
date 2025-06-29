package com.example.quizapp.adminActivities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.LoginActivity;
import com.example.quizapp.R;
import com.example.quizapp.SessionManager;

public class BaseAdminActivity extends AppCompatActivity {//checked

    protected void setupToolbar(int toolbarId) {
        Toolbar toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.item_students) {
            //Log.d("TAG_Manage_Students", "item_students");
            Intent intent = new Intent(this, ManageStudentsActivity.class);
            startActivity(intent);
            finish();
        }
        if (item.getItemId()==R.id.item_courses) {
            Intent intent = new Intent(this, ManageCoursesActivity.class);
            startActivity(intent);
            finish();
        }
        if (item.getItemId()==R.id.item_logout) {
            //Log.d("TAG_Account_Admin", "item_logout");
            SessionManager.logout(this);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
           // finish();
           // finish();
        }
        return true;
    }
}