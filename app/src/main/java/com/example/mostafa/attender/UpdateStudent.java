package com.example.mostafa.attender;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateStudent extends AppCompatActivity {
    EditText studentID;
    TextView studentName, noClass;
    Button edit, chooseDate, save, delete;
    RadioGroup radioGroup;
    RadioButton absentBtn, presentBtn, selectedButton;
    View studentDataFrame;
    Calendar calendar;
    String selectedData, selectedYear, month, day, subjectName, updatedStudentName = "", date, attendanceStatues, ID;
    DateFormat dateFormat;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    DataSnapshot targetStudent;
    Student student, studetUpdated, studentState;
    List<Subject> studentSubjects;
    boolean studentExist, subjectExist, dateExist, updateValid;
    ArrayList<String> attendanceDates;
    String[] splitDate;
    int subjectIndex, attendanceDateIndex, absentCount, attendCount;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_student);
        studentID = (EditText) findViewById(R.id.student_updateID);
        studentName = (TextView) findViewById(R.id.update_student_name);
        noClass = (TextView) findViewById(R.id.no_classID);
        edit = (Button) findViewById(R.id.editStudent_id);
        chooseDate = (Button) findViewById(R.id.choose_dateID);
        save = (Button) findViewById(R.id.done_edit);
        delete = (Button) findViewById(R.id.deleteStudent_id);
        studentState = new Student();
        radioGroup = (RadioGroup) findViewById(R.id.radioAttendance);
        absentBtn = (RadioButton) findViewById(R.id.radioAbsent);
        presentBtn = (RadioButton) findViewById(R.id.radioPresent);
        studentDataFrame = (View) findViewById(R.id.dataFrame);
        studentDataFrame.setVisibility(View.GONE);
        scrollView = (ScrollView) findViewById(R.id.scroll_id);
        dateFormat = DateFormat.getDateTimeInstance();
        subjectName = getIntent().getExtras().getString("subject");
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        student = new Student();
        studetUpdated = new Student();
        studentSubjects = new ArrayList<>();
        attendanceDates = new ArrayList<>();
        splitDate = new String[2];
        radioGroup.setVisibility(View.GONE);
        noClass.setVisibility(View.GONE);
        if (savedInstanceState != null) {
            studentExist = savedInstanceState.getBoolean("exist", false);
            dateExist = savedInstanceState.getBoolean("dateExist", false);
            studentState = savedInstanceState.getParcelable("student");
            if (studentExist) {
                studentName.setText(studentState.getName());
                studentDataFrame.setVisibility(View.VISIBLE);
            }
            if (dateExist) {
                radioGroup.setVisibility(View.VISIBLE);
                if (savedInstanceState.getString("checked").equals("present")) {
                    presentBtn.setChecked(true);
                } else if (savedInstanceState.getString("checked").equals("absent")) {
                    absentBtn.setChecked(true);
                }
                updateValid = savedInstanceState.getBoolean("update", false);
                studetUpdated = savedInstanceState.getParcelable("studentUpdate");
            } else {
                noClass.setVisibility(View.VISIBLE);
            }
            selectedData = savedInstanceState.getString("date", "-");
            attendanceStatues = savedInstanceState.getString("statues", "-");
            subjectIndex = savedInstanceState.getInt("subIndex");
            attendanceDateIndex = savedInstanceState.getInt("attendIndex");
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("scrollState",
                new int[]{scrollView.getScrollX(), scrollView.getScrollY()});
        if (presentBtn.isChecked()) {
            outState.putString("checked", "present");
        } else if (absentBtn.isChecked()) {
            outState.putString("checked", "absent");
        }
        outState.putParcelable("student", studentState);
        outState.putParcelable("studentUpdate", studetUpdated);
        outState.putBoolean("exist", studentExist);
        outState.putBoolean("dateExist", dateExist);
        outState.putBoolean("update", updateValid);
        outState.putString("date", selectedData);
        outState.putString("statues", attendanceStatues);
        outState.putInt("subIndex", subjectIndex);
        outState.putInt("attendIndex", attendanceDateIndex);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("scrollState");
        if (position != null)
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.scrollTo(position[0], position[1]);
                }
            });
    }

    public void checkID() {
        studentSubjects.clear();
        studentExist = subjectExist = false;
        reference.child("student").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    student = snapshot.getValue(Student.class);
                    studentSubjects = student.getSubjects();
                    if (student.getID().equals(ID)) {
                        for (int i = 0; i < studentSubjects.size(); i++) {
                            if (studentSubjects.get(i).getName().equals(subjectName)) {
                                subjectExist = true;
                                studentState = student;
                                break;
                            }
                        }
                        if (subjectExist) {
                            studentExist = true;
                            updatedStudentName = student.getName();
                            break;
                        }
                    }
                }
                if (studentExist) {   // if entered ID is correct
                    studentDataFrame.setVisibility(View.VISIBLE);
                    studentName.setText(getString(R.string.student_name)+" : " + updatedStudentName);
                } else { // if ID is not exist in firebase database after check id in arraylist of students in database
                    Toast.makeText(getApplicationContext(), getString(R.string.update_student_false), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void editStudent(View view)  // make student data frame visible
    {
        radioGroup.setVisibility(View.GONE);
        noClass.setVisibility(View.GONE);
        ID = studentID.getText().toString();
        if (TextUtils.isEmpty(ID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.data_miss), Toast.LENGTH_SHORT).show();
        } else {
            checkID();
        }
    }

    public void dataPicker(View view) {
        ID = studentID.getText().toString();
        if (TextUtils.isEmpty(ID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.data_miss), Toast.LENGTH_SHORT).show();
        } else {
            calendar = Calendar.getInstance();
            new DatePickerDialog(this, d, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear++;
            day = String.valueOf(dayOfMonth);
            month = String.valueOf(monthOfYear);
            selectedYear = String.valueOf(year);
            if (dayOfMonth < 10) {
                day = '0' + String.valueOf(dayOfMonth);
            }
            if (monthOfYear < 10) {
                month = '0' + String.valueOf(monthOfYear);
            }
            selectedData = day + '-' + month + '-' + year; // then split this string with char '-' to get (d,m,y) and compare them with list of dates if exist get the last char of date in list it will be (absent or present)
            getDateValidation();
        }
    };

    public void getDateValidation() {
        studentSubjects.clear();
        dateExist = false;
        reference.child("student").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    student = snapshot.getValue(Student.class);
                    studentSubjects = student.getSubjects();
                    if (student.getID().equals(ID)) {
                        for (int i = 0; i < studentSubjects.size(); i++) {
                            if (studentSubjects.get(i).getName().equals(subjectName)) {
                                attendanceDates = studentSubjects.get(i).getAttendanceDate();
                                for (int j = 0; j < attendanceDates.size(); j++) {
                                    splitDate = attendanceDates.get(j).split(",");
                                    date = splitDate[0];
                                    attendanceStatues = splitDate[1];
                                    if (date.equals(selectedData)) {
                                        subjectIndex = i;
                                        attendanceDateIndex = j;
                                        dateExist = true;
                                        studetUpdated = student;
                                        break;
                                    }
                                }
                                if (dateExist) {
                                    break;
                                }
                            }
                            if (dateExist) {
                                break;
                            }
                        }
                        if (dateExist) {
                            ID = student.getID();
                            break;
                        }
                    }
                }
                if (dateExist) {
                    noClass.setVisibility(View.GONE);
                    radioGroup.setVisibility(View.VISIBLE);
                    if (attendanceStatues.equals("F"))
                        absentBtn.setChecked(true);
                    else
                        presentBtn.setChecked(true);
                    updateValid = true;
                } else {  // if ID is not exist in firebase database after check id in arraylist of students in database
                    noClass.setVisibility(View.VISIBLE);
                    radioGroup.setVisibility(View.GONE);
                    updateValid = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteStudent(View view) {
        ID = studentID.getText().toString();
        if (TextUtils.isEmpty(ID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.data_miss), Toast.LENGTH_SHORT).show();
        } else {
            subjectExist = false;
            studentSubjects.clear();
            Query q1 = reference.child("student").orderByChild("id").equalTo(ID);
            q1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null)
                        Toast.makeText(getApplicationContext(), getString(R.string.update_student_false), Toast.LENGTH_SHORT).show();
                    else {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            student = snapshot.getValue(Student.class);
                            studentSubjects = student.getSubjects();
                            for (int i = 0; i < studentSubjects.size(); i++) {
                                if (studentSubjects.get(i).getName().equals(subjectName)) {
                                    subjectExist = true;
                                    targetStudent = snapshot;
                                    studentSubjects.remove(i);
                                    break;
                                }
                            }
                        }
                        if (subjectExist) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateStudent.this);
                            builder.setMessage(getString(R.string.delete_student))
                                    .setTitle(getString(R.string.delete_title))
                                    .setIcon(R.drawable.deleteicon)
                                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            targetStudent.getRef().child("subjects").setValue(studentSubjects);
                                            Toast.makeText(getApplicationContext(), getString(R.string.student_deleted), Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.update_student_false), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void doneEdit(View view) {
        ID = studentID.getText().toString();
        if (TextUtils.isEmpty(ID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.data_miss), Toast.LENGTH_SHORT).show();
        } else if (updateValid) {
            String attendanceDate;
            int selectedId = radioGroup.getCheckedRadioButtonId();
            selectedButton = (RadioButton) findViewById(selectedId);
            if (attendanceStatues.equals("T") && selectedButton.getText().equals("Absent")) {
                studentSubjects = studetUpdated.getSubjects();
                absentCount = studentSubjects.get(subjectIndex).getAbsentCount();
                attendCount = studentSubjects.get(subjectIndex).getAttendCount();
                absentCount++;
                attendCount--;
                studentSubjects.get(subjectIndex).setAbsentCount(absentCount);
                studentSubjects.get(subjectIndex).setAttendCount(attendCount);
                attendanceDates = studentSubjects.get(subjectIndex).getAttendanceDate();
                attendanceDate = attendanceDates.get(attendanceDateIndex);
                attendanceDate = attendanceDate.substring(0, attendanceDate.length() - 1) + "F";
                attendanceDates.set(attendanceDateIndex, attendanceDate);
                studentSubjects.get(subjectIndex).setAttendanceDate(attendanceDates);
                attendanceStatues = "F";
                updateStudentAttendance();
            } else if (attendanceStatues.equals("F") && selectedButton.getText().equals("Present")) {
                studentSubjects = studetUpdated.getSubjects();
                absentCount = studentSubjects.get(subjectIndex).getAbsentCount();
                attendCount = studentSubjects.get(subjectIndex).getAttendCount();
                absentCount--;
                attendCount++;
                studentSubjects.get(subjectIndex).setAbsentCount(absentCount);
                studentSubjects.get(subjectIndex).setAttendCount(attendCount);
                attendanceDates = studentSubjects.get(subjectIndex).getAttendanceDate();
                attendanceDate = attendanceDates.get(attendanceDateIndex);
                attendanceDate = attendanceDate.substring(0, attendanceDate.length() - 1) + "T";
                attendanceDates.set(attendanceDateIndex, attendanceDate);
                studentSubjects.get(subjectIndex).setAttendanceDate(attendanceDates);
                attendanceStatues = "T";
                updateStudentAttendance();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_change), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_class), Toast.LENGTH_LONG).show();
        }
    }

    public void updateStudentAttendance() {
        Query q1 = reference.child("student").orderByChild("id").equalTo(ID);
        q1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    student = snapshot.getValue(Student.class);
                    snapshot.getRef().child("subjects").setValue(studentSubjects);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Toast.makeText(getApplicationContext(), getString(R.string.update_student_attendance), Toast.LENGTH_SHORT).show();
    }
}
