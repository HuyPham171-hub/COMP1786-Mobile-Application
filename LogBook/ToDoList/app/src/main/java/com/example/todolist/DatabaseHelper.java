package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "task_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tasks";

    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_DEADLINE = "deadline";
    private static final String COL_DURATION = "duration";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_COMPLETED = "completed";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT," +
                COL_DEADLINE + " TEXT," +
                COL_DURATION + " INTEGER," +
                COL_DESCRIPTION + " TEXT," +
                COL_COMPLETED + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, task.name);
        values.put(COL_DEADLINE, new SimpleDateFormat("yyyy-MM-dd").format(task.deadline));
        values.put(COL_DURATION, task.duration);
        values.put(COL_DESCRIPTION, task.descriptions);
        values.put(COL_COMPLETED, task.completed ? 1 : 0);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                Date deadline = new Date();
                try {
                    deadline = sdf.parse(cursor.getString(2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int duration = cursor.getInt(3);
                String desc = cursor.getString(4);
                boolean completed = cursor.getInt(5) == 1;
                Task task = new Task(name, deadline, duration, desc, completed);
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return taskList;
    }

    public void updateTaskCompletion(String taskName, boolean completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COMPLETED, completed ? 1 : 0);
        db.update(TABLE_NAME, values, COL_NAME + " = ?", new String[]{taskName});
        db.close();
    }
}
