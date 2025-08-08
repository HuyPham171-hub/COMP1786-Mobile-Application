package com.example.yogaadminapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClassInstanceActivity extends AppCompatActivity {

    Button btnAddInstance, btnSearchInstance;
    EditText editSearchTeacherInstance, editSearchDateInstance;
    ListView listViewInstances;

    DatabaseHelper dbHelper;
    InstanceAdapter instanceAdapter;
    List<ClassInstance> instanceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_instance);

        // Initialize views
        btnAddInstance = findViewById(R.id.btnAddInstance);
        btnSearchInstance = findViewById(R.id.btnSearchInstance);
        editSearchTeacherInstance = findViewById(R.id.editSearchTeacherInstance);
        editSearchDateInstance = findViewById(R.id.editSearchDateInstance);
        listViewInstances = findViewById(R.id.listViewInstances);

        dbHelper = new DatabaseHelper(this);
        instanceList = dbHelper.getAllClassInstances(); // Fetch from DB

        instanceAdapter = new InstanceAdapter(this, instanceList);
        listViewInstances.setAdapter(instanceAdapter);

        // Add Instance Button
        btnAddInstance.setOnClickListener(v -> {
            Intent intent = new Intent(ClassInstanceActivity.this, CreateInstanceActivity.class);
            startActivity(intent);
        });

        // Search Button logic
        btnSearchInstance.setOnClickListener(v -> filterInstances());

        // List item click (optional - e.g., view/edit instance)
        listViewInstances.setOnItemClickListener((parent, view, position, id) -> {
            ClassInstance selectedInstance = instanceList.get(position);
            Intent intent = new Intent(ClassInstanceActivity.this, ViewInstanceDetailActivity.class);
            intent.putExtra("instance_id", selectedInstance.getId());
            startActivity(intent);
        });

        editSearchDateInstance.setOnClickListener(v -> showDatePicker());
    }

    @Override
    protected void onResume() {
        super.onResume();
        instanceList = dbHelper.getAllClassInstances();
        instanceAdapter.updateData(instanceList);
    }

    private void filterInstances() {
        String teacher = editSearchTeacherInstance.getText().toString().toLowerCase();
        String date = editSearchDateInstance.getText().toString().trim();

        List<ClassInstance> filtered = new ArrayList<>();
        for (ClassInstance instance : instanceList) {
            boolean matches = true;

            if (!teacher.isEmpty() && !instance.getTeacher().toLowerCase().contains(teacher)) {
                matches = false;
            }
            if (!date.isEmpty() && !instance.getDate().equalsIgnoreCase(date)) {
                matches = false;
            }
            if (matches) filtered.add(instance);
        }
        instanceAdapter.updateData(filtered);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format(Locale.ENGLISH, "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    editSearchDateInstance.setText(formattedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

}
