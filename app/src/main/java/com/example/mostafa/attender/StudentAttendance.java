package com.example.mostafa.attender;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StudentAttendance extends AppCompatActivity {
    TextView studentID, studentName;
    List<Subject> subjects, studentSubjects;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Student student;
    AttendanceAdapter attendanceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);
        studentName = (TextView) findViewById(R.id.student_name);
        recyclerView = (RecyclerView) findViewById(R.id.attendance_recyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        studentID = (TextView) findViewById(R.id.student_id);
        getStudentData();
        setAdapter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("saveState", recyclerView.getLayoutManager().onSaveInstanceState());
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("saveState");
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    public void getStudentData() {
        student = getIntent().getExtras().getParcelable("student");
        subjects = new ArrayList<>();
        studentSubjects = new ArrayList<>();
        subjects = student.getSubjects();
        for (int i = 0; i < subjects.size(); i++) {
            studentSubjects.add(subjects.get(i));

        }
        studentName.setText(getString(R.string.student_name) + " : " + student.getName());
        studentID.setText(getString(R.string.enter_id) + " : " + student.getID());
    }

    public void setAdapter() {
        attendanceAdapter = new AttendanceAdapter(studentSubjects, getApplicationContext());
        recyclerView.setAdapter(attendanceAdapter);
    }
}
