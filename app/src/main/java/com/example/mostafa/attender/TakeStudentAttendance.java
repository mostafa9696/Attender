package com.example.mostafa.attender;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;

import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TakeStudentAttendance extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    Student student;
    List<Subject> studentSubjects;
    ArrayList<Student> students;
    TakeAttendanceAdapter takeAttendanceAdapter;
    boolean exist, attendanceStatues, checkState;
    Map<String, Boolean> map, tmpMap;
    ArrayList<String> attendanceDates, radioStatues;
    int newAbsentCount, newPresentCount;
    String date, tmpDate, studentID, subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_student_attendance);
        recyclerView = (RecyclerView) findViewById(R.id.take_attendance_recyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        subjectName = getIntent().getExtras().getString("subject");
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        checkState = false;
        radioStatues = new ArrayList<>();
        students = new ArrayList<>();
        if (savedInstanceState != null) {
            checkState = savedInstanceState.getBoolean("state");
            radioStatues = savedInstanceState.getStringArrayList("studentList");
            students = savedInstanceState.getParcelableArrayList("students");
        }
        getStudents();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        tmpMap = new HashMap<String, Boolean>();
        tmpMap = takeAttendanceAdapter.getAttendceMap();
        ArrayList<String> attendanceTmp = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : tmpMap.entrySet()) {
            if (entry.getValue()) {
                attendanceTmp.add(entry.getKey() + ",t");
            } else {
                attendanceTmp.add(entry.getKey() + ",f");
            }
        }
        outState.putStringArrayList("studentList", attendanceTmp);
        outState.putBoolean("state", true);
        outState.putParcelableArrayList("students", students);
        outState.putParcelable("saveState", recyclerView.getLayoutManager().onSaveInstanceState());
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("saveState");
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    public void getStudents() {
        if (!checkState) {
            student = new Student();
            studentSubjects = new ArrayList<>();
            reference.child("student").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        exist = false;
                        student = snapshot.getValue(Student.class);
                        studentSubjects = student.getSubjects();
                        for (int i = 0; i < studentSubjects.size(); i++) {
                            if (studentSubjects.get(i).getName().equals(subjectName)) {
                                exist = true;
                                break;
                            }
                        }
                        if (exist) {
                            students.add(student);
                            takeAttendanceAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        takeAttendanceAdapter = new TakeAttendanceAdapter(getApplicationContext(), students, radioStatues);
        recyclerView.setAdapter(takeAttendanceAdapter);
    }

    public void save(View view) {
        map = new HashMap<String, Boolean>();
        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        map = takeAttendanceAdapter.getAttendceMap();
        boolean checkAll = map.get("getAllstudentAttendance");
        if (checkAll) {
            student = new Student();
            studentSubjects = new ArrayList<>();
            attendanceDates = new ArrayList<>();
            reference.child("student").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        student = snapshot.getValue(Student.class);
                        studentID = student.getID();
                        if (map.containsKey(studentID)) {
                            studentSubjects = student.getSubjects();
                            for (int i = 0; i < studentSubjects.size(); i++) {
                                if (studentSubjects.get(i).getName().equals(subjectName)) {
                                    attendanceStatues = map.get(studentID);
                                    if (studentSubjects.get(i).getAttendanceDate() != null) {
                                        attendanceDates = studentSubjects.get(i).getAttendanceDate();
                                    }
                                    if (attendanceStatues) {
                                        tmpDate = date + ',' + 'T';
                                        attendanceDates.add(tmpDate);
                                        newPresentCount = studentSubjects.get(i).getAttendCount() + 1;
                                        studentSubjects.get(i).setAttendCount(newPresentCount);
                                        studentSubjects.get(i).setAttendanceDate(attendanceDates);
                                    } else {
                                        tmpDate = date + ',' + 'F';
                                        attendanceDates.add(tmpDate);
                                        newAbsentCount = studentSubjects.get(i).getAbsentCount() + 1;
                                        studentSubjects.get(i).setAbsentCount(newAbsentCount);
                                        studentSubjects.get(i).setAttendanceDate(attendanceDates);
                                    }
                                    snapshot.getRef().child("subjects").setValue(studentSubjects);
                                    break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Toast.makeText(getApplicationContext(), getString(R.string.attendance_taken), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.attendance_check), Toast.LENGTH_LONG).show();
        }

    }

}
