package com.example.yogaadminapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UniversalYoga.db";
    private static final int DATABASE_VERSION = 1;

    // Table Course
    public static final String TABLE_COURSE = "course";
    public static final String COLUMN_COURSE_ID = "course_id";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_SKILL_LEVEL = "skill_level";

    // Table ClassInstance
    public static final String TABLE_INSTANCE = "class_instance";
    public static final String COLUMN_INSTANCE_ID = "instance_id";
    public static final String COLUMN_COURSE_REF_ID = "course_ref_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TEACHER = "teacher";
    public static final String COLUMN_COMMENT = "comment";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCourseTable = "CREATE TABLE " + TABLE_COURSE + " (" +
                COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY + " TEXT NOT NULL, " +
                COLUMN_TIME + " TEXT NOT NULL, " +
                COLUMN_CAPACITY + " INTEGER NOT NULL, " +
                COLUMN_DURATION + " INTEGER NOT NULL, " +
                COLUMN_PRICE + " REAL NOT NULL, " +
                COLUMN_TYPE + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_SKILL_LEVEL + " TEXT" + ");";

        String createInstanceTable = "CREATE TABLE " + TABLE_INSTANCE + " (" +
                COLUMN_INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COURSE_REF_ID + " INTEGER NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TEACHER + " TEXT NOT NULL, " +
                COLUMN_COMMENT + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_COURSE_REF_ID + ") REFERENCES " + TABLE_COURSE + "(" + COLUMN_COURSE_ID + ") ON DELETE CASCADE);";

        db.execSQL(createCourseTable);
        db.execSQL(createInstanceTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop and recreate schema changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        onCreate(db);
    }

    // Insert Course
    public long insertCourse(String day, String time, int capacity, int duration,
                             double price, String type, String description, String skillLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY, day);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_CAPACITY, capacity);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_SKILL_LEVEL, skillLevel);

        long newId = db.insert(TABLE_COURSE, null, values);
        db.close();
        return newId;
    }


    public Course getCourseById(int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COURSE + " WHERE " + COLUMN_COURSE_ID + "=?", new String[]{String.valueOf(courseId)});

        Course course = null;

        if (cursor.moveToFirst()) {
            String day = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
            int capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            String skillLevel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SKILL_LEVEL));

            course = new Course(courseId, day, time, capacity, duration, price, type, description, skillLevel);
        }

        cursor.close();
        return course;
    }

    // Get All Courses
    public Cursor getAllCourses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_COURSE + " ORDER BY " + COLUMN_COURSE_ID + " DESC", null);
    }

    // Delete a Course
    public boolean deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_COURSE, COLUMN_COURSE_ID + "=?", new String[]{String.valueOf(courseId)});
        return deletedRows > 0;
    }

    // Insert Class Instance
    public long insertClassInstance(int courseId, String date, String teacher, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_REF_ID, courseId);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TEACHER, teacher);
        values.put(COLUMN_COMMENT, comment);

        long newId = db.insert(TABLE_INSTANCE, null, values);
        db.close();
        return newId;
    }

    public ClassInstance getInstanceById(int instanceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ClassInstance instance = null;

        String query = "SELECT ci.*, c." + COLUMN_TYPE + " AS course_type " +
                "FROM " + TABLE_INSTANCE + " ci " +
                "JOIN " + TABLE_COURSE + " c ON ci." + COLUMN_COURSE_REF_ID + " = c." + COLUMN_COURSE_ID +
                " WHERE ci." + COLUMN_INSTANCE_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(instanceId)});

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_ID));
            int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_REF_ID));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
            String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER));
            String comment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT));
            String courseType = cursor.getString(cursor.getColumnIndexOrThrow("course_type"));

            instance = new ClassInstance(id, courseId, courseType, date, teacher, comment);
            cursor.close();
        }

        return instance;
    }

    public List<ClassInstance> getAllClassInstances() {
        List<ClassInstance> instanceList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT i." + COLUMN_INSTANCE_ID + ", i." + COLUMN_COURSE_REF_ID + ", i." + COLUMN_DATE +
                ", i." + COLUMN_TEACHER + ", i." + COLUMN_COMMENT + ", c." + COLUMN_TYPE +
                " FROM " + TABLE_INSTANCE + " i JOIN " + TABLE_COURSE + " c ON i." + COLUMN_COURSE_REF_ID + "=c." + COLUMN_COURSE_ID +
                " ORDER BY i." + COLUMN_DATE;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_ID));
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_REF_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT));
                String courseType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));

                ClassInstance instance = new ClassInstance(id, courseId, courseType, date, teacher, comment);
                instanceList.add(instance);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return instanceList;
    }

    public boolean deleteInstance(int instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_INSTANCE, COLUMN_INSTANCE_ID + "=?", new String[]{String.valueOf(instanceId)});
        return deletedRows > 0;
    }

    // Get Instances for a Course
    public Cursor getInstancesByCourse(int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INSTANCE + " WHERE " + COLUMN_COURSE_REF_ID + "=? ORDER BY " + COLUMN_DATE, new String[]{String.valueOf(courseId)});
    }

    public List<ClassInstance> getInstancesByCourseId(int courseId) {
        List<ClassInstance> instances = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT i." + COLUMN_INSTANCE_ID + ", i." + COLUMN_DATE + ", i." + COLUMN_TEACHER +
                ", i." + COLUMN_COMMENT + ", c." + COLUMN_TYPE +
                " FROM " + TABLE_INSTANCE + " i JOIN " + TABLE_COURSE + " c ON i." + COLUMN_COURSE_REF_ID + "=c." + COLUMN_COURSE_ID +
                " WHERE i." + COLUMN_COURSE_REF_ID + "=? ORDER BY i." + COLUMN_DATE;

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(courseId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT));
                String courseType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));

                ClassInstance instance = new ClassInstance(id, courseId, courseType, date, teacher, comment);
                instances.add(instance);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return instances;
    }

    // Update Class Instance
    public boolean updateClassInstance(int instanceId, int courseId, String date, String teacher, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_REF_ID, courseId);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TEACHER, teacher);
        values.put(COLUMN_COMMENT, comment);

        int updatedRows = db.update(TABLE_INSTANCE, values, COLUMN_INSTANCE_ID + "=?", new String[]{String.valueOf(instanceId)});
        return updatedRows > 0;
    }

    // Update Course
    public boolean updateCourse(int courseId, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        int updatedRows = db.update(TABLE_COURSE, values, COLUMN_COURSE_ID + "=?", new String[]{String.valueOf(courseId)});
        return updatedRows > 0;
    }

    public List<com.example.yogaadminapp.Course> getAllCoursesList() {
        List<com.example.yogaadminapp.Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COURSE + " ORDER BY " + COLUMN_COURSE_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID));
                String day = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                int capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String skillLevel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SKILL_LEVEL));

                com.example.yogaadminapp.Course course = new com.example.yogaadminapp.Course(id, day, time, capacity, duration, price, type, description, skillLevel);
                courseList.add(course);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return courseList;
    }
}
