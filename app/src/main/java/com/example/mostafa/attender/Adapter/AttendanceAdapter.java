package com.example.mostafa.attender.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mostafa.attender.Model.Subject;
import com.example.mostafa.attender.R;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.CustomViewHolder> {
    private List<Subject> subjects;
    public Context context;

    public AttendanceAdapter(List<Subject> subjects, Context context) {
        this.subjects = subjects;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_item, parent, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(AttendanceAdapter.CustomViewHolder holder, int position) {
        holder.bind(subjects.get(position), context);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView subjectName, absentCount, presentCount;

        public CustomViewHolder(View itemView) {
            super(itemView);
            this.subjectName = (TextView) itemView.findViewById(R.id.subject_attendance_name);
            this.absentCount = (TextView) itemView.findViewById(R.id.absent_attendance_count);
            this.presentCount = (TextView) itemView.findViewById(R.id.present_attendance_count);
        }

        public void bind(final Subject subject, Context con) {
            subjectName.setText(con.getResources().getString(R.string.enter_subject) + " : " + subject.getName());
            absentCount.setText(con.getResources().getString(R.string.absent) + " : " + String.valueOf(subject.getAbsentCount()) + " " + con.getResources().getString(R.string.times));
            presentCount.setText(con.getResources().getString(R.string.attend) + " : " + String.valueOf(subject.getAttendCount()) + " " + con.getResources().getString(R.string.times));
        }
    }
}
