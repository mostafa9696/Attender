package com.example.mostafa.attender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mostafa.attender.Activity.StudentLogin;
import com.example.mostafa.attender.Activity.TeacherLogin;


public class MainActivityFragment extends Fragment {

    Button studentBtn;
    Button teacherBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_activity, container, false);
        studentBtn = (Button) root.findViewById(R.id.studentLogin);
        teacherBtn = (Button) root.findViewById(R.id.teacherLogin);
        studentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StudentLogin.class);
                startActivity(intent);
            }
        });
        teacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TeacherLogin.class);
                startActivity(intent);
            }
        });
        return root;
    }

}
