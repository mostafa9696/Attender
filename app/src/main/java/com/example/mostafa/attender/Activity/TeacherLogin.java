package com.example.mostafa.attender.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mostafa.attender.R;
import com.example.mostafa.attender.Model.Teacher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherLogin extends AppCompatActivity {
    View logInFrame, registerFrame;
    Teacher teacher, loginTeacher;
    TextView registerText;
    EditText loginName, loginPassword, registerName, registerPassword, registerSubject, registerConfirmPassword;
    Button loginIn, signIn;
    String name, password, confirmPassword, subject;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    Animation bounceAnimation;
    boolean exist = false, flag = false, loginFrame;
    ScrollView scrollView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
        logInFrame = (View) findViewById(R.id.login_frame_id);
        registerFrame = (View) findViewById(R.id.register_frame_id);
        registerText = (TextView) findViewById(R.id.register_text_id);
        loginName = (EditText) findViewById(R.id.teacher_name_loginId);
        loginPassword = (EditText) findViewById(R.id.teacher_pass_loginId);
        registerName = (EditText) findViewById(R.id.teacher_name_registerId);
        registerPassword = (EditText) findViewById(R.id.teacher_password_registerId);
        registerSubject = (EditText) findViewById(R.id.teacher_subject_registerId);
        registerConfirmPassword = (EditText) findViewById(R.id.teacher_confirmPassword_registerId);
        loginIn = (Button) findViewById(R.id.loginIn_id);
        signIn = (Button) findViewById(R.id.signIn_id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        scrollView = (ScrollView) findViewById(R.id.scroll_id);
        bounceAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        progressDialog = new ProgressDialog(TeacherLogin.this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(getString(R.string.logging_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        loginFrame = true;
        if (savedInstanceState != null) {
            loginFrame = savedInstanceState.getBoolean("frame", true);
        }
        if (!loginFrame) {
            logInFrame.setVisibility(View.GONE);
            registerFrame.setVisibility(View.VISIBLE);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("scrollState",
                new int[]{scrollView.getScrollX(), scrollView.getScrollY()});
        outState.putBoolean("frame", loginFrame);

    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("scrollState");
        if (position != null) {
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.scrollTo(position[0], position[1]);
                }
            });
        }
    }

    public void register(View view)   // for registerText
    {
        logInFrame.setVisibility(View.GONE);
        registerFrame.setVisibility(View.VISIBLE);
        registerFrame.startAnimation(bounceAnimation);
        loginFrame = false;
    }

    public void checkRegister() {
        progressDialog.show();
        new Thread() {
            public void run() {
                exist = false;
                reference.child("teacher").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            teacher = snapshot.getValue(Teacher.class);
                            if (teacher.getName().equals(name)) {
                                exist = true;
                            }
                        }
                        if (exist && flag == false) {
                            Toast.makeText(getApplicationContext(), getString(R.string.name_exist), Toast.LENGTH_SHORT).show();
                            flag = true;
                        } else if (flag == false) {
                            teacher = new Teacher(name, password, subject);
                            reference.child("teacher").push().setValue(teacher);
                            Intent intent = new Intent(getApplicationContext(), TeacherFunctions.class);
                            intent.putExtra("teacher", teacher);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), getString(R.string.creat_email), Toast.LENGTH_SHORT).show();
                            flag = true;
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }.start();
    }

    public void checkLogin() {
        progressDialog.show();
        new Thread() {
            public void run() {
                exist = false;
                loginTeacher = new Teacher();
                reference.child("teacher").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            teacher = snapshot.getValue(Teacher.class);
                            if (teacher.getName().equals(name) && teacher.getPassword().equals(password)) {
                                exist = true;
                                loginTeacher = teacher;
                            }
                        }
                        if (exist && flag == false) {
                            Intent intent = new Intent(getApplicationContext(), TeacherFunctions.class);
                            intent.putExtra("teacher", loginTeacher);
                            startActivity(intent);
                            flag = true;
                            Toast.makeText(getApplicationContext(), getString(R.string.welcome_message) + " " + loginTeacher.getName(), Toast.LENGTH_SHORT).show();
                        } else if (flag == false) {
                            Toast.makeText(getApplicationContext(), getString(R.string.invalid_login), Toast.LENGTH_SHORT).show();
                            flag = true;
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }.start();
    }

    public void signIn(View view) {
        boolean checkConnection = isNetworkConnected();
        if (checkConnection == true) {
            flag = false;
            name = registerName.getText().toString();
            password = registerPassword.getText().toString();
            confirmPassword = registerConfirmPassword.getText().toString();
            subject = registerSubject.getText().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(subject)) {
                Toast.makeText(getApplicationContext(), getString(R.string.data_miss), Toast.LENGTH_SHORT).show();
            } else if (!confirmPassword.equals(password)) {
                Toast.makeText(getApplicationContext(), getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
            } else {
                checkRegister();
            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    public void login(View view) {
        boolean checkConnection = isNetworkConnected();
        if (checkConnection == true) {
            flag = false;
            name = loginName.getText().toString();
            password = loginPassword.getText().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), getString(R.string.data_miss), Toast.LENGTH_SHORT).show();
            } else if (true)  // teacher is not exist in firebase
            {
                checkLogin();
            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
    }
}
