package com.example.yogaadminapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class InstanceAdapter extends BaseAdapter {

    private Context context;
    private List<ClassInstance> instanceList;
    private LayoutInflater inflater;

    public InstanceAdapter(Context context, List<ClassInstance> instanceList) {
        this.context = context;
        this.instanceList = instanceList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return instanceList.size();
    }

    @Override
    public Object getItem(int position) {
        return instanceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return instanceList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.instance_list_item, parent, false);
            holder = new ViewHolder();
            holder.tvCourseType = convertView.findViewById(R.id.tvCourseType);
            holder.tvDateTeacher = convertView.findViewById(R.id.tvDateTeacher);
            holder.tvComment = convertView.findViewById(R.id.tvComment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ClassInstance instance = instanceList.get(position);

        holder.tvCourseType.setText(instance.getCourseType());
        holder.tvDateTeacher.setText(instance.getDate() + " | " + instance.getTeacher());
        holder.tvComment.setText(instance.getComment().isEmpty() ? "No comments" : instance.getComment());

        return convertView;
    }

    public void updateData(List<ClassInstance> newList) {
        instanceList.clear();
        instanceList.addAll(newList);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView tvCourseType;
        TextView tvDateTeacher;
        TextView tvComment;
    }
}
