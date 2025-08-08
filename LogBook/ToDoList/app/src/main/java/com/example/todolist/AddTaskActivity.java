package com.example.todolist;

import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class AddTaskActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        dbHelper = new DatabaseHelper(this);

        DatePicker dp = findViewById(R.id.dpDeadline);
        Calendar c = Calendar.getInstance();
        dp.init(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH), null);
    }

    public void onClickAddTask(View v) throws ParseException {
        String n, des;
        Date dl;
        int d;
        n = ((EditText)findViewById(R.id.etTaskName)).getText().toString();
        des = ((EditText)findViewById(R.id.etmDescriptions)).getText().toString();
        DatePicker dp = (DatePicker)findViewById(R.id.dpDeadline);
        d = Integer.valueOf(((EditText)findViewById(R.id.etDuration)).getText().toString());
        String dateText = String.valueOf(dp.getDayOfMonth()) + "/" +
                            String.valueOf(dp.getMonth() + 1) + "/" +
                            String.valueOf(dp.getYear());

        Task t = new Task(n, new SimpleDateFormat("dd/MM/yyyy").parse(dateText), d, des, false);
        dbHelper.addTask(t);
        MainActivity.taskList.add(t);
        Toast.makeText(getApplicationContext(),"A task is just created", Toast.LENGTH_SHORT).show();

        finish();
    }
}