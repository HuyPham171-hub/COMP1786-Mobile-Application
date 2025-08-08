package com.example.yogaadminapp;

import com.example.yogaadminapp.Course;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

public class CourseAdapter extends ArrayAdapter<Course> {

    private Context mContext;
    private List<Course> courseList;

    public CourseAdapter(Context context, List<Course> list) {
        super(context, 0, list);
        mContext = context;
        courseList = list;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Course course = courseList.get(position);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.course_list_item, parent, false);

        TextView tvType = convertView.findViewById(R.id.tvCourseType);
        TextView tvDayTime = convertView.findViewById(R.id.tvCourseDayTime);
        TextView tvCapacityDuration = convertView.findViewById(R.id.tvCourseCapacityDuration);
        TextView tvPrice = convertView.findViewById(R.id.tvCoursePrice);
        TextView tvSkillLevel = convertView.findViewById(R.id.tvSkillLevel);
        TextView tvDescription = convertView.findViewById(R.id.tvCourseDescription);



        tvType.setText(course.getType());
        tvDayTime.setText(course.getDayOfWeek() + " at " + course.getTime());
        tvCapacityDuration.setText(course.getCapacity() + " people " + course.getDuration() + " mins");
        tvPrice.setText("£" + course.getPrice());
        tvSkillLevel.setText("Skill level: " + course.getSkillLevel());

        if (course.getDescription() != null && !course.getDescription().isEmpty()) {
            tvDescription.setText(course.getDescription());
        } else {
            tvDescription.setText("No description provided.");
        }

        return convertView;
    }

    // Update adapter data
    public void updateData(List<Course> newCourses) {
        courseList.clear();
        courseList.addAll(newCourses);
        notifyDataSetChanged();
    }
}
