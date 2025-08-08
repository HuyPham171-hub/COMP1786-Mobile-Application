package com.example.yogaadminapp;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateYogaCourseActivity extends AppCompatActivity {

    private Spinner spinnerDay, spinnerTime, spinnerCapacity, spinnerDuration, spinnerType, spinnerSkillLevel;
    private EditText editPrice, editDescription;
    private Button btnSubmit, btnCancelCourse;

    DatabaseHelper dbHelper;
    int courseId = -1;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_yoga_course);

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Initiailize UI elements
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerTime = findViewById(R.id.spinnerTime);
        spinnerCapacity = findViewById(R.id.spinnerCapacity);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerSkillLevel = findViewById(R.id.spinnerSkillLevel);
        editPrice = findViewById(R.id.editPrice);
        editDescription = findViewById(R.id.editDescription);
        btnSubmit = findViewById(R.id.btnSubmitCourse);
        btnCancelCourse = findViewById(R.id.btnCancelCourse);

        // Populate Spinners
        setupSpinners();

        if (getIntent().hasExtra("course_id")) {
            courseId = getIntent().getIntExtra("course_id", -1);
            isEditMode = true;
            loadCourseData(courseId);
            btnSubmit.setText("Update Course");
        }

        // Submit button logic
        btnSubmit.setOnClickListener(view -> handleSubmit());

        // Cancel button
        btnCancelCourse.setOnClickListener(v -> finish());
    }

    private void setupSpinners()
    {
        // Day of the week
        spinnerDay.setAdapter(ArrayAdapter.createFromResource(this, R.array.days_array, android.R.layout.simple_spinner_dropdown_item));

        // Time options
        spinnerTime.setAdapter(ArrayAdapter.createFromResource(this, R.array.times_array, android.R.layout.simple_spinner_dropdown_item));

        // Capacity options
        spinnerCapacity.setAdapter(ArrayAdapter.createFromResource(this, R.array.capacity_array, android.R.layout.simple_spinner_dropdown_item));

        // Duration options
        spinnerDuration.setAdapter(ArrayAdapter.createFromResource(this, R.array.duration_array, android.R.layout.simple_spinner_dropdown_item));

        // Type of class
        spinnerType.setAdapter(ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_dropdown_item));

        // Skill level
        spinnerSkillLevel.setAdapter(ArrayAdapter.createFromResource(this, R.array.skill_level_array, android.R.layout.simple_spinner_dropdown_item));
    }

    private void loadCourseData(int id) {
        for (Course c : dbHelper.getAllCoursesList()) {
            if (c.getId() == id) {
                // Set spinner positions
                setSpinnerSelection(spinnerDay, c.getDayOfWeek());
                setSpinnerSelection(spinnerTime, c.getTime());
                setSpinnerSelection(spinnerCapacity, String.valueOf(c.getCapacity()));
                setSpinnerSelection(spinnerDuration, String.valueOf(c.getDuration()));
                setSpinnerSelection(spinnerType, c.getType());
                setSpinnerSelection(spinnerSkillLevel, c.getSkillLevel());

                editPrice.setText(String.valueOf(c.getPrice()));
                editDescription.setText(c.getDescription());
                break;
            }
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void checkDayChangeAndUpdateCourse(String newDay, ContentValues values) {
        Course oldCourse = null;
        for (Course c : dbHelper.getAllCoursesList()) {
            if (c.getId() == courseId) {
                oldCourse = c;
                break;
            }
        }

        if (oldCourse != null && !oldCourse.getDayOfWeek().equals(newDay)) {
            int instanceCount = dbHelper.getInstancesByCourse(courseId).getCount();
            if (instanceCount > 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("This course has " + instanceCount + " class instances.\n" +
                                "Changing the day may cause mismatch with scheduled dates.\n\nDo you want to continue?")
                        .setPositiveButton("Continue", (dialog, which) -> {
                            updateCourseInDatabase(values);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return; // Stop here until user confirms
            }
        }

        // If no mismatch or no instances, just update normally
        updateCourseInDatabase(values);
    }

    private void updateCourseInDatabase(ContentValues values) {
        boolean updated = dbHelper.updateCourse(courseId, values);
        if (updated) {
            Toast.makeText(this, "Course updated!", Toast.LENGTH_LONG).show();
            uploadCourseToCloud(courseId);
            finish();
        } else {
            Toast.makeText(this, "Update failed.", Toast.LENGTH_LONG).show();
        }
    }

    private void handleSubmit()
    {
        String day = spinnerDay.getSelectedItem().toString();
        String time = spinnerTime.getSelectedItem().toString();
        String capacityStr = spinnerCapacity.getSelectedItem().toString();
        String durationStr = spinnerDuration.getSelectedItem().toString();
        String priceStr = editPrice.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String description = editDescription.getText().toString().trim();
        String skillLevel = spinnerSkillLevel.getSelectedItem().toString();

        // Validation

        if (priceStr.isEmpty())
        {
            editPrice.setError("Price is required");
            editPrice.requestFocus();
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            editPrice.setError("Enter a valid price (> 0)");
            editPrice.requestFocus();
            return;
        }

        // Confirmation message
        String confirmation = "Please confirm the details:\n\n" +
                "Day: " + day + "\n" +
                "Time: " + time + "\n" +
                "Capacity: " + capacityStr + "\n" +
                "Duration: " + durationStr + " minutes\n" +
                "Price: £" + price + "\n" +
                "Type: " + type + "\n" +
                "Skill Level: " + skillLevel + "\n" +
                "Description: " + (description.isEmpty() ? "N/A" : description);

        new AlertDialog.Builder(this)
                .setTitle("Confirm Course")
                .setMessage(confirmation)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    if (isEditMode) {
                        // UPDATE MODE
                        ContentValues values = new ContentValues();
                        values.put(DatabaseHelper.COLUMN_DAY, day);
                        values.put(DatabaseHelper.COLUMN_TIME, time);
                        values.put(DatabaseHelper.COLUMN_CAPACITY, Integer.parseInt(capacityStr));
                        values.put(DatabaseHelper.COLUMN_DURATION, Integer.parseInt(durationStr));
                        values.put(DatabaseHelper.COLUMN_PRICE, price);
                        values.put(DatabaseHelper.COLUMN_TYPE, type);
                        values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
                        values.put(DatabaseHelper.COLUMN_SKILL_LEVEL, skillLevel);

                        // Check day change and ask user
                        checkDayChangeAndUpdateCourse(day, values);
                    } else {
                        // INSERT MODE
                        long newId = dbHelper.insertCourse(day, time, Integer.parseInt(capacityStr),
                                Integer.parseInt(durationStr), price, type, description, skillLevel);
                        if (newId != -1) {
                            Toast.makeText(this, "Course added successfully!", Toast.LENGTH_LONG).show();

                            // Upload course to cloud
                            uploadCourseToCloud((int)newId);
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to add course.", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Edit", null)
                .show();

    }

    private void uploadCourseToCloud(int courseId) {
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        Course c = dbHelper.getCourseById(courseId);
        if (c == null) return;

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

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("courses")
                .document(String.valueOf(c.getId()))
                .set(courseMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Course uploaded to cloud!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload course.", Toast.LENGTH_SHORT).show());
    }
}