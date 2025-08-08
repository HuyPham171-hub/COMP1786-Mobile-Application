package com.example.yogaadminapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewCourseDetailActivity extends AppCompatActivity {

    TextView tvCourseType, tvDayTime, tvCapacityDuration, tvPrice, tvSkillLevel, tvDescription;
    Button btnEditCourse, btnDeleteCourse;
    ListView listViewInstances;
    InstanceAdapter instanceAdapter;
    List<ClassInstance> instanceList;

    DatabaseHelper dbHelper;
    int courseId;
    Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course_detail);

        dbHelper = new DatabaseHelper(this);

        tvCourseType = findViewById(R.id.tvCourseType);
        tvDayTime = findViewById(R.id.tvDayTime);
        tvCapacityDuration = findViewById(R.id.tvCapacityDuration);
        tvPrice = findViewById(R.id.tvPrice);
        tvSkillLevel = findViewById(R.id.tvSkillLevel);
        tvDescription = findViewById(R.id.tvDescription);

        btnEditCourse = findViewById(R.id.btnEditCourse);
        btnDeleteCourse = findViewById(R.id.btnDeleteCourse);

        // Get courseId from Intent
        courseId = getIntent().getIntExtra("course_id", -1);
        if (courseId == -1) {
            Toast.makeText(this, "Invalid course.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch Course object from DB
        course = dbHelper.getCourseById(courseId);
        if (course == null) {
            Toast.makeText(this, "Course not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayCourseDetails();

        btnEditCourse.setOnClickListener(v -> {
            Intent intent = new Intent(ViewCourseDetailActivity.this, CreateYogaCourseActivity.class);
            intent.putExtra("edit_mode", true);
            intent.putExtra("course_id", courseId);
            startActivity(intent);
        });

        btnDeleteCourse.setOnClickListener(v -> confirmDelete());

        listViewInstances = findViewById(R.id.listViewInstancesForCourse);
        dbHelper = new DatabaseHelper(this);

        // Get courseId
        int courseId = getIntent().getIntExtra("course_id", -1);

        // Get instances from DB by courseId
        instanceList = dbHelper.getInstancesByCourseId(courseId);
        instanceAdapter = new InstanceAdapter(this, instanceList);
        listViewInstances.setAdapter(instanceAdapter);

        listViewInstances.setOnItemClickListener((parent, view, position, id) -> {
            ClassInstance selectedInstance = instanceList.get(position);
            Intent intent = new Intent(this, ViewInstanceDetailActivity.class);
            intent.putExtra("instance_id", selectedInstance.getId());
            startActivity(intent);
        });

    }

    private void displayCourseDetails() {
        tvCourseType.setText("Type: " + course.getType());
        tvDayTime.setText("Day/Time: " + course.getDayOfWeek() + " at " + course.getTime());
        tvCapacityDuration.setText("Capacity: " + course.getCapacity() + " | Duration: " + course.getDuration() + " mins");
        tvPrice.setText("Price: £" + course.getPrice());
        tvSkillLevel.setText("Skill Level: " + course.getSkillLevel());
        tvDescription.setText("Description: " + (course.getDescription().isEmpty() ? "N/A" : course.getDescription()));
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course? This will also delete all instances.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteCourse(courseId);
                    if (deleted) {
                        Toast.makeText(this, "Course deleted.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete course.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        course = dbHelper.getCourseById(courseId);
        if (course != null) {
            displayCourseDetails();
        }

        instanceList = dbHelper.getInstancesByCourseId(courseId);
        instanceAdapter.updateData(instanceList);
    }
}
