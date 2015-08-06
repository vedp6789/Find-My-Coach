package com.findmycoach.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.findmycoach.app.beans.student.Grade;

import java.util.ArrayList;

/**
 * Created by abhi7 on 06/08/15.
 */
public class GradeAdapter extends ArrayAdapter<Grade> {

    private Context context;
    private ArrayList<Grade> gradeArrayList;
    private  int id;
    public GradeAdapter(Context context, int textViewResourceId,
                       ArrayList<Grade> gradeArrayList) {
        super(context, textViewResourceId, gradeArrayList);
        this.context = context;
        id=textViewResourceId;
        this.gradeArrayList = gradeArrayList;
    }

    public int getCount(){
        return gradeArrayList.size();
    }

    public Grade getItem(int position){
        return gradeArrayList.get(position);
    }

    public long getItemId(int position){
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView)LayoutInflater.from(context).inflate(id,parent,false);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(gradeArrayList.get(position).getGrade());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView)LayoutInflater.from(context).inflate(id,parent,false);
        label.setTextColor(Color.BLACK);
        label.setText(gradeArrayList.get(position).getGrade());
        return label;
    }
}