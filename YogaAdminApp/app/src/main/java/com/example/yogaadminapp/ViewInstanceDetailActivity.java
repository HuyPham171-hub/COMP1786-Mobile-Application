package com.example.yogaadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewInstanceDetailActivity extends AppCompatActivity {

    TextView tvCourseType, tvDate, tvTeacher, tvComment;
    Button btnEditInstance, btnDeleteInstance;

    DatabaseHelper dbHelper;
    int instanceId;
    ClassInstance instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_instance_detail);

        tvCourseType = findViewById(R.id.tvCourseType);
        tvDate = findViewById(R.id.tvDate);
        tvTeacher = findViewById(R.id.tvTeacher);
        tvComment = findViewById(R.id.tvComment);

        btnEditInstance = findViewById(R.id.btnEditInstance);
        btnDeleteInstance = findViewById(R.id.btnDeleteInstance);

        dbHelper = new DatabaseHelper(this);

        if (getIntent().hasExtra("instance_id")) {
            instanceId = getIntent().getIntExtra("instance_id", -1);
            loadInstance(instanceId);
        } else {
            Toast.makeText(this, "Invalid instance ID.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnEditInstance.setOnClickListener(v -> {
            Intent intent = new Intent(ViewInstanceDetailActivity.this, CreateInstanceActivity.class);
            intent.putExtra("instance_id", instanceId);
            startActivity(intent);
        });

        btnDeleteInstance.setOnClickListener(v -> confirmDelete());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInstance(instanceId); // Refresh data after edit
    }

    private void loadInstance(int id) {
        List<ClassInstance> allInstances = dbHelper.getAllClassInstances();
        for (ClassInstance inst : allInstances) {
            if (inst.getId() == id) {
                instance = inst;
                break;
            }
        }

        if (instance != null) {
            tvCourseType.setText("Course Type: " + instance.getCourseType());
            tvDate.setText("Date: " + instance.getDate());
            tvTeacher.setText("Teacher: " + instance.getTeacher());
            tvComment.setText("Comment: " + (instance.getComment().isEmpty() ? "N/A" : instance.getComment()));
        } else {
            Toast.makeText(this, "Instance not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Instance")
                .setMessage("Are you sure you want to delete this class instance?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteInstance(instanceId);
                    if (deleted) {
                        Toast.makeText(this, "Instance deleted.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
