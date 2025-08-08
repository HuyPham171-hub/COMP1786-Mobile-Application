package com.example.yogaadminapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateInstanceActivity extends AppCompatActivity {

    Spinner spinnerSelectCourse;
    EditText editDateInstance, editTeacherInstance, editCommentInstance;
    Button btnSaveInstance, btnCancelInstance;

    DatabaseHelper dbHelper;
    List<Course> courseList;
    ArrayAdapter<String> courseAdapter;

    int instanceId = -1;
    boolean isEditMode = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_instance);

        // Initialize views
        spinnerSelectCourse = findViewById(R.id.spinnerSelectCourse);
        editDateInstance = findViewById(R.id.editDateInstance);
        editTeacherInstance = findViewById(R.id.editTeacherInstance);
        editCommentInstance = findViewById(R.id.editCommentInstance);
        btnSaveInstance = findViewById(R.id.btnSaveInstance);
        btnCancelInstance = findViewById(R.id.btnCancelInstance);

        dbHelper = new DatabaseHelper(this);

        // Load courses into spinner
        courseList = dbHelper.getAllCoursesList();
        if (courseList.isEmpty()) {
            Toast.makeText(this, "No courses available. Please add a course first.", Toast.LENGTH_LONG).show();
            finish(); // Close activity if no courses exist
            return;
        }

        // Map Course list to display strings
        String[] courseNames = new String[courseList.size()];
        for (int i = 0; i < courseList.size(); i++) {
            Course c = courseList.get(i);
            courseNames[i] = c.getType() + " - " + c.getDayOfWeek() + " " + c.getTime();
        }

        courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courseNames);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectCourse.setAdapter(courseAdapter);

        if (getIntent().hasExtra("instance_id")) {
            instanceId = getIntent().getIntExtra("instance_id", -1);
            isEditMode = true;
            loadInstanceData(instanceId);
            btnSaveInstance.setText("Update Instance");
        }

        editDateInstance.setOnClickListener(v -> showDatePicker());

        // Save button logic
        btnSaveInstance.setOnClickListener(v -> saveInstance());

        // Cancel button
        btnCancelInstance.setOnClickListener(v -> finish());
    }

    private void loadInstanceData(int id) {
        List<ClassInstance> allInstances = dbHelper.getAllClassInstances();
        for (ClassInstance inst : allInstances) {
            if (inst.getId() == id) {
                editDateInstance.setText(inst.getDate());
                editTeacherInstance.setText(inst.getTeacher());
                editCommentInstance.setText(inst.getComment());

                // Set course spinner position
                for (int i = 0; i < courseList.size(); i++) {
                    if (courseList.get(i).getId() == inst.getCourseId()) {
                        spinnerSelectCourse.setSelection(i);
                        break;
                    }
                }
                break;
            }
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format selected date as dd/MM/yyyy
                    String formattedDate = String.format(Locale.ENGLISH, "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    editDateInstance.setText(formattedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void saveInstance() {
        int selectedCourseIndex = spinnerSelectCourse.getSelectedItemPosition();
        Course selectedCourse = courseList.get(selectedCourseIndex);
        int courseId = selectedCourse.getId();

        String date = editDateInstance.getText().toString().trim();
        String teacher = editTeacherInstance.getText().toString().trim();
        String comment = editCommentInstance.getText().toString().trim();

        if (date.isEmpty() || teacher.isEmpty()) {
            Toast.makeText(this, "Date and Teacher are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        String actualDayOfWeek = getDayOfWeekFromDate(date);
        if (actualDayOfWeek == null) {
            Toast.makeText(this, "Invalid date format. Use dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        String expectedDayOfWeek = selectedCourse.getDayOfWeek();  // from Course
        if (!actualDayOfWeek.equalsIgnoreCase(expectedDayOfWeek)) {
            Toast.makeText(this, "Date does not match the course's day (" + expectedDayOfWeek + ")", Toast.LENGTH_LONG).show();
            return;
        }

        if (isEditMode) {
            boolean updated = dbHelper.updateClassInstance(instanceId, courseId, date, teacher, comment);
            if (updated) {
                Toast.makeText(this, "Instance updated!", Toast.LENGTH_SHORT).show();
                uploadInstanceToCloud(instanceId);
                finish();
            } else {
                Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            long newId = dbHelper.insertClassInstance(courseId, date, teacher, comment);
            if (newId != -1) {
                Toast.makeText(this, "Instance created successfully!", Toast.LENGTH_SHORT).show();

                uploadInstanceToCloud((int)newId);
                finish();
            } else {
                Toast.makeText(this, "Failed to create instance.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getDayOfWeekFromDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            Date date = sdf.parse(dateString);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            return dayFormat.format(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private void uploadInstanceToCloud(int instanceId) {
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        ClassInstance inst = dbHelper.getInstanceById(instanceId); 
        if (inst == null) return;

        Map<String, Object> instMap = new HashMap<>();
        instMap.put("id", inst.getId());
        instMap.put("courseId", inst.getCourseId());
        instMap.put("date", inst.getDate());
        instMap.put("teacher", inst.getTeacher());
        instMap.put("comment", inst.getComment());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("instances")
                .document(String.valueOf(inst.getId()))
                .set(instMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Instance uploaded to cloud!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload instance.", Toast.LENGTH_SHORT).show());
    }


}
