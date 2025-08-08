package com.example.yogaadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.yogaadminapp.DatabaseHelper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogaadminapp.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    Button btnAddCourse, btnViewSchedule, btnUploadToCloud;
    Spinner spinnerSearchDay;
    ListView listViewCourses;

    com.example.yogaadminapp.DatabaseHelper dbHelper;
    com.example.yogaadminapp.CourseAdapter adapter;
    List<com.example.yogaadminapp.Course> courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();

//        setupUploadButton();

        // Initialize views
        btnAddCourse = findViewById(R.id.btnAddCourse);
        btnViewSchedule = findViewById(R.id.btnViewSchedule);
        btnUploadToCloud = findViewById(R.id.btnUploadToCloud);
        spinnerSearchDay = findViewById(R.id.spinnerSearchDay);
        listViewCourses = findViewById(R.id.listViewCourses);

        dbHelper = new DatabaseHelper(this);
        courseList = dbHelper.getAllCoursesList(); // Load all courses from DB

        // Setup Adapter
        adapter = new CourseAdapter(this, courseList);
        listViewCourses.setAdapter(adapter);

        listViewCourses.setOnItemClickListener((parent, view, position, id) -> {
            Course selectedCourse = courseList.get(position);
            Intent intent = new Intent(MainActivity.this, ViewCourseDetailActivity.class);
            intent.putExtra("course_id", selectedCourse.getId());
            startActivity(intent);
        });

        // Button: Add Course
        btnAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateYogaCourseActivity.class);
            startActivity(intent);
        });

        // Button: View Schedule
        btnViewSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ClassInstanceActivity.class);
            startActivity(intent);
        });

        btnUploadToCloud.setOnClickListener(v -> uploadAllCoursesAndInstances());


        // Search by Day Spinner
        spinnerSearchDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterCourses();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


    }

    // Called every time activity resumes
    @Override
    protected void onResume() {
        super.onResume();
        courseList = dbHelper.getAllCoursesList();
        adapter.updateData(courseList);  // refresh adapter data
    }

    // Filter courses based on teacher name, day, and date
    private void filterCourses() {
        String selectedDay = spinnerSearchDay.getSelectedItem().toString();

        // If "All Days" selected, show all courses
        if (selectedDay.equals("All Days")) {
            adapter.updateData(courseList);
            return;
        }

        List<Course> filtered = new ArrayList<>();
        for (Course course : courseList) {
            if (course.getDayOfWeek().equalsIgnoreCase(selectedDay)) {
                filtered.add(course);
            }
        }
        adapter.updateData(filtered);
    }

    // Upload all courses and instances to Firestore
    private void uploadAllCoursesAndInstances() {
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        List<Course> courses = dbHelper.getAllCoursesList();
        List<ClassInstance> instances = dbHelper.getAllClassInstances();

        // Upload Courses
        for (Course c : courses) {
            Map<String, Object> courseMap = new HashMap<>();
            courseMap.put("id", c.getId());
            courseMap.put("type", c.getType());
            courseMap.put("dayOfWeek", c.getDayOfWeek());
            courseMap.put("time", c.getTime());
            courseMap.put("capacity", c.getCapacity());
            courseMap.put("duration", c.getDuration());
            courseMap.put("price", c.getPrice());
            courseMap.put("skillLevel", c.getSkillLevel());
            courseMap.put("description", c.getDescription());

            firestore.collection("courses")
                    .document(String.valueOf(c.getId()))
                    .set(courseMap);
        }

        // Upload Class Instances
        for (ClassInstance inst : instances) {
            Map<String, Object> instMap = new HashMap<>();
            instMap.put("id", inst.getId());
            instMap.put("courseId", inst.getCourseId());
            instMap.put("date", inst.getDate());
            instMap.put("teacher", inst.getTeacher());
            instMap.put("comment", inst.getComment());

            firestore.collection("instances")
                    .document(String.valueOf(inst.getId()))
                    .set(instMap);
        }

        Toast.makeText(this, "All data uploaded to cloud!", Toast.LENGTH_SHORT).show();
    }
}
