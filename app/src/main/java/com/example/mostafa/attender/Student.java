package com.example.mostafa.attender;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class Student implements Parcelable {
    private String name;
    private String ID;
    private List<Subject> subjects;

    public Student(String ID, String name, List<Subject> subjects) {
        this.ID = ID;
        this.name = name;
        this.subjects = subjects;
    }

    public Student() {
        name = ID = "";
        subjects = new ArrayList<>();
    }

    protected Student(Parcel in) {
        subjects = new ArrayList<>();
        name = in.readString();
        ID = in.readString();
        subjects = in.readArrayList(Subject.class.getClassLoader());
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(ID);
        dest.writeList(subjects);
    }
}
