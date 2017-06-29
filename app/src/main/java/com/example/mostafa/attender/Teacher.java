package com.example.mostafa.attender;

import android.os.Parcel;
import android.os.Parcelable;


public class Teacher implements Parcelable {
    private String userName;
    private String subject;
    private String password;

    public Teacher(String name, String password, String subject) {
        this.userName = name;
        this.password = password;
        this.subject = subject;
    }

    public Teacher() {
    }

    protected Teacher(Parcel in) {
        userName = in.readString();
        subject = in.readString();
        password = in.readString();
    }

    public static final Creator<Teacher> CREATOR = new Creator<Teacher>() {
        @Override
        public Teacher createFromParcel(Parcel in) {
            return new Teacher(in);
        }

        @Override
        public Teacher[] newArray(int size) {
            return new Teacher[size];
        }
    };

    public void setName(String name) {
        this.userName = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {

        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(subject);
        dest.writeString(password);

    }
}
