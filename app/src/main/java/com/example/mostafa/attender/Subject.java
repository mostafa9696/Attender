package com.example.mostafa.attender;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Subject implements Parcelable {
    private String name;
    private int absentCount;
    private int attendCount;
    private ArrayList<String> attendanceDate; // each element has date + (Y or N) split with char '+' to get data & attendance statue

    Subject() {
        name = "";
        absentCount = attendCount = 0;
        attendanceDate = new ArrayList<>();
    }

    public Subject(int absentCount, int attendCount, String name, ArrayList<String> attendanceDate) {
        this.absentCount = absentCount;
        this.attendanceDate = attendanceDate;
        this.attendCount = attendCount;
        this.name = name;
    }

    protected Subject(Parcel in) {
        name = in.readString();
        absentCount = in.readInt();
        attendCount = in.readInt();
        attendanceDate = in.createStringArrayList();
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    public ArrayList<String> getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(ArrayList<String> attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public int getAttendCount() {
        return attendCount;
    }

    public String getName() {
        return name;
    }

    public void setAbsentCount(int absentCount) {
        this.absentCount = absentCount;
    }

    public void setAttendCount(int attendCount) {
        this.attendCount = attendCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(absentCount);
        dest.writeInt(attendCount);
        dest.writeStringList(attendanceDate);
    }
}
