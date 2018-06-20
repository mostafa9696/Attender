package com.example.mostafa.attender.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mostafa.attender.Activity.AddStudent;
import com.example.mostafa.attender.Activity.TakeStudentAttendance;
import com.example.mostafa.attender.Activity.UpdateStudent;
import com.example.mostafa.attender.Model.Teacher;
import com.example.mostafa.attender.R;

import me.yugy.github.reveallayout.RevealLayout;


public class TeacherFunctions extends AppCompatActivity {
    Button updattBtn, attendanceBtn;
    FloatingActionButton addStudent;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Teacher teacher;
    RevealLayout revealLayout;
    View revealView;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_functions);
        addStudent = (FloatingActionButton) findViewById(R.id.add_fab);
        teacher = getIntent().getExtras().getParcelable("teacher");
        updattBtn = (Button) findViewById(R.id.update_id);
        attendanceBtn = (Button) findViewById(R.id.take_attendance_id);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setTitle(teacher.getName());
        revealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
        revealView = findViewById(R.id.reveal_view);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_teacher_functions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        coordinatorLayout.setVisibility(View.VISIBLE);
    }

    public void updateStudent(View view) {
        boolean checkConnection = isNetworkConnected();
        if (checkConnection == true) {
            Intent intent = new Intent(getApplicationContext(), UpdateStudent.class);
            intent.putExtra("subject", teacher.getSubject());  // to update specific attendance for this subject
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    public void takeAttendance(View view) {
        boolean checkConnection = isNetworkConnected();
        if (checkConnection == true) {
            Intent intent = new Intent(getApplicationContext(), TakeStudentAttendance.class);
            intent.putExtra("subject", teacher.getSubject());
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    public void addStudent(View myview) {
        addStudent.setClickable(false);
        int[] location = new int[2];
        addStudent.getLocationInWindow(location);
        location[0] += addStudent.getWidth() / 2;
        location[1] += addStudent.getHeight() / 2;
        final Intent intent = new Intent(getApplicationContext(), AddStudent.class);
        revealView.setVisibility(View.VISIBLE);
        revealLayout.setVisibility(View.VISIBLE);
        revealLayout.show(location[0], location[1]);
        addStudent.postDelayed(new Runnable() {
            @Override
            public void run() {
                intent.putExtra("subject", teacher.getSubject());
                startActivity(intent);
                overridePendingTransition(0, R.anim.reveal);
            }
        }, 600);
        addStudent.postDelayed(new Runnable() {
            @Override
            public void run() {
                addStudent.setClickable(true);
                revealLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.GONE);
                revealView.setVisibility(View.GONE);
            }
        }, 605);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
    }
}
