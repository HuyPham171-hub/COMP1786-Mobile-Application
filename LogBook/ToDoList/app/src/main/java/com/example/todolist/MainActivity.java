
package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Task> taskList = new ArrayList<Task>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickAdd(View v){
        Intent i = new Intent(getApplicationContext(), AddTaskActivity.class);
        startActivity(i);
    }

    protected void onStart(){
        super.onStart();
        ListView lv = findViewById(R.id.listViewTasks);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        taskList = dbHelper.getAllTasks();

        TaskAdapter adapter = new TaskAdapter(this, taskList);
        lv.setAdapter(adapter);
    }

    public class TaskAdapter extends ArrayAdapter<Task>{
        public TaskAdapter(Context context, ArrayList<Task> tasks) { super(context, 0, tasks); }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Task t = getItem(position);
            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item,parent,false);
            TextView tvTaskName = (TextView)convertView.findViewById(R.id.tvTaskName);
            TextView tvDeadline = (TextView)convertView.findViewById(R.id.tvDeadline);
            TextView tvDuration = (TextView)convertView.findViewById(R.id.tvDuration);
            TextView tvDescriptions = (TextView)convertView.findViewById(R.id.tvDescriptions);
            TextView tvStatus = convertView.findViewById(R.id.tvCompleted);
            CheckBox checkBox = convertView.findViewById(R.id.checkBoxCompleted);

            tvTaskName.setText(t.name);
            tvDeadline.setText(t.deadline.toString().substring(0,10));
            tvDuration.setText(String.valueOf(t.duration));
            tvDescriptions.setText(String.valueOf(t.descriptions));
            tvStatus.setText(t.completed ? "Completed" : "Incomplete");

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(t.completed);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                t.completed = isChecked;
                tvStatus.setText(isChecked ? "Completed" : "Incomplete");

                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                dbHelper.updateTaskCompletion(t.name, isChecked);

            });
            return convertView;
        }
    }
}