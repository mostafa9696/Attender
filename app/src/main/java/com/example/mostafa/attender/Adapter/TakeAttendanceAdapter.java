package com.example.mostafa.attender.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.mostafa.attender.Model.Student;
import com.example.mostafa.attender.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TakeAttendanceAdapter extends RecyclerView.Adapter<TakeAttendanceAdapter.CustomViewHolder> {
    private static ArrayList<Student> students;
    private static ArrayList<String> radioStatues;
    private static String[] split;
    public Context context;
    public static Map<String, Boolean> attendceMap, tmpMap;

    public TakeAttendanceAdapter(Context context, ArrayList<Student> students, ArrayList<String> radioStatues) {
        this.context = context;
        this.students = students;
        split = new String[2];
        attendceMap = new HashMap<String, Boolean>();
        tmpMap = new HashMap<String, Boolean>();
        attendceMap.put("getAllstudentAttendance", false);
        for (int i = 0; i < radioStatues.size(); i++) {
            split = radioStatues.get(i).split(",");

            if (split[1].equals("t")) {
                tmpMap.put(split[0], true);
            } else
                tmpMap.put(split[0], false);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.take_attendance_item, parent, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.bind(students.get(position), context);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView studentName, studentID;
        protected RadioButton absentRadio, presentRadio;
        protected RadioGroup radioGroup;

        public CustomViewHolder(View itemView) {
            super(itemView);
            this.studentName = (TextView) itemView.findViewById(R.id.student_attendance_name);
            this.studentID = (TextView) itemView.findViewById(R.id.student_attendance_ID);
            this.absentRadio = (RadioButton) itemView.findViewById(R.id.radio_absent);
            this.presentRadio = (RadioButton) itemView.findViewById(R.id.radio_present);
            this.radioGroup = (RadioGroup) itemView.findViewById(R.id.take_attendance_radio);
            this.setIsRecyclable(false);
        }

        public void bind(final Student student, Context con) {
            studentName.setText(student.getName());
            studentID.setText(con.getString(R.string.id) + " : " + student.getID());
            radioGroup.setOnCheckedChangeListener(null);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.radio_absent) {
                        attendceMap.put(student.getID(), false);
                    } else if (checkedId == R.id.radio_present) {
                        attendceMap.put(student.getID(), true);
                    }
                }
            });
            if (attendceMap.containsKey(student.getID())) {
                if (attendceMap.get(student.getID())) {
                    absentRadio.setChecked(false);
                    presentRadio.setChecked(true);
                } else {
                    absentRadio.setChecked(true);
                    presentRadio.setChecked(false);
                }
            } else if (tmpMap.containsKey(student.getID())) {
                if (tmpMap.get(student.getID())) {
                    absentRadio.setChecked(false);
                    presentRadio.setChecked(true);
                } else {
                    absentRadio.setChecked(true);
                    presentRadio.setChecked(false);
                }
            } else {
                absentRadio.setChecked(false);
                presentRadio.setChecked(false);
            }
        }
    }

    public static Map<String, Boolean> getAttendceMap() {
        if ((attendceMap.size() - 1) == students.size() || (attendceMap.size() - 1 + tmpMap.size()) == students.size()) {
            attendceMap.put("getAllstudentAttendance", true);
        }
        return attendceMap;
    }
}
