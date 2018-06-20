package com.example.mostafa.attender.Activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mostafa.attender.R;
import com.example.mostafa.attender.Model.Student;
import com.example.mostafa.attender.Model.Subject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddStudent extends AppCompatActivity {
    EditText studentName, studentID;
    Button add;
    Student student;
    String subjectName = "", ID, name;
    List<Subject> studentsubjects, registerStudentsubjects;
    ArrayList<String> attendanceDates;
    Subject subject;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    boolean IDexist, flag, subjectExist, studentExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        studentName = (EditText) findViewById(R.id.student_nameID);
        studentID = (EditText) findViewById(R.id.studentId);
        add = (Button) findViewById(R.id.addStudent_id);
        subjectName = getIntent().getExtras().getString("subject");
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
    }

    public void checkRegister() {
        studentExist = subjectExist = IDexist = false;
        registerStudentsubjects = new ArrayList<>();
        studentsubjects = new ArrayList<>();
        student = new Student();
        reference.child("student").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    student = snapshot.getValue(Student.class);
                    studentsubjects = student.getSubjects();
                    if (student.getID().equals(ID) && !student.getName().equals(name)) {
                        IDexist = true;
                    } else if (student.getID().equals(ID) && student.getName().equals(name)) {
                        studentExist = true;
                        for (int i = 0; i < studentsubjects.size(); i++) {
                            if (studentsubjects.get(i).getName().equals(subjectName)) {
                                subjectExist = true;
                                break;
                            }
                        }
                        if (!subjectExist) {
                            registerStudentsubjects = student.getSubjects();
                        }
                    }

                }
                Log.d("ee1", subjectName + " , " + String.valueOf(subjectExist) + " , " + String.valueOf(studentExist));
                if (IDexist && !flag)  // ID exist before in firebase and the name related to this id is incorrect with the name in firebase
                {
                    flag = true;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.ID_exist), Toast.LENGTH_SHORT).show();
                } else if (studentExist == false && flag == false)// student not exist in database , so create new student object with only one subject in arraylist <subject>
                {
                    flag = true;
                    attendanceDates = new ArrayList<>();
                    studentsubjects = new ArrayList<>();
                    subject = new Subject(0, 0, subjectName, attendanceDates);
                    studentsubjects.add(subject);
                    student = new Student(ID, name, studentsubjects);
                    reference.child("student").push().setValue(student);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.student_added), Toast.LENGTH_SHORT).show();
                    studentName.getText().clear();
                    studentID.getText().clear();
                } else if (subjectExist && flag == false)  // ID & name exist in firebase and this subject is exist in student's arraylist<subjects>
                {
                    flag = true;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.subject_exist), Toast.LENGTH_SHORT).show();
                } else if (subjectExist == false && flag == false) // ID & name exist in firebase and this subject not exist in student's arraylist<subjects> , so add this subject in arraylist and update this student
                {
                    flag = true;
                    subject = new Subject(0, 0, subjectName, attendanceDates);
                    registerStudentsubjects.add(subject);
                    Query q1 = reference.child("student").orderByChild("id").equalTo(ID);     // Get student to make update on him
                    q1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                student = snapshot.getValue(Student.class);
                                snapshot.getRef().child("subjects").setValue(registerStudentsubjects);
                            }
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.student_enroll_course), Toast.LENGTH_SHORT).show();
                            studentName.getText().clear();
                            studentID.getText().clear();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addStudent(View view) {
        boolean checkConnection = isNetworkConnected();
        if (checkConnection == true) {
            flag = false;
            ID = studentID.getText().toString();
            name = studentName.getText().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ID)) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_miss), Toast.LENGTH_SHORT).show();
            } else {
                checkRegister();
            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
    }
}
