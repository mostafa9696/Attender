package com.example.mostafa.attender.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mostafa.attender.R;
import com.example.mostafa.attender.Model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentLogin extends AppCompatActivity {
    EditText studetnID;
    Button login;
    boolean flag, exist;
    Student student, loginStudent;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    String ID;
    ProgressDialog progressDialog;
    Animation bounceAnimation;
    LinearLayout studentLoginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        studentLoginLayout = (LinearLayout) findViewById(R.id.activity_student_login);
        bounceAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        studentLoginLayout.setAnimation(bounceAnimation);
        studetnID = (EditText) findViewById(R.id.student_loginId);
        login = (Button) findViewById(R.id.loginIn_id);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        progressDialog = new ProgressDialog(StudentLogin.this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(getString(R.string.logging_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        studetnID.getText().clear();
    }

    public void checkLogin() {
        exist = false;
        student = new Student();
        loginStudent = new Student();
        progressDialog.show();
        new Thread() {
            public void run() {
                reference.child("student").
                        addListenerForSingleValueEvent(new ValueEventListener() {
                                                           @Override
                                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                                               for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                   student = snapshot.getValue(Student.class);
                                                                   if (student.getID().equals(ID)) {
                                                                       exist = true;
                                                                       loginStudent = student;
                                                                       break;
                                                                   }
                                                               }
                                                               if (exist && flag == false) {
                                                                   flag = true;
                                                                   Intent intent = new Intent(getApplicationContext(), StudentAttendance.class);
                                                                   intent.putExtra("name", loginStudent.getName());
                                                                   intent.putExtra("id", loginStudent.getID());
                                                                   intent.putExtra("student", loginStudent);
                                                                   progressDialog.dismiss();
                                                                   Toast.makeText(getApplicationContext(), getString(R.string.welcome_message) + " " + student.getName(), Toast.LENGTH_SHORT).show();
                                                                   startActivity(intent);
                                                               } else if (flag == false) {
                                                                   progressDialog.dismiss();
                                                                   Toast.makeText(getApplicationContext(), getString(R.string.invalid_login_student), Toast.LENGTH_SHORT).show();
                                                                   flag = true;
                                                               }
                                                           }

                                                           @Override
                                                           public void onCancelled(DatabaseError databaseError) {
                                                           }
                                                       }
                        );
            }
        }.start();
    }

    public void login(View view) {
        boolean checkConnection = isNetworkConnected();
        if (checkConnection == true) {
            ID = studetnID.getText().toString();
            if (TextUtils.isEmpty(ID)) {
                Toast.makeText(getApplicationContext(), getString(R.string.data_miss), Toast.LENGTH_SHORT).show();
            } else {
                flag = false;
                checkLogin();
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
