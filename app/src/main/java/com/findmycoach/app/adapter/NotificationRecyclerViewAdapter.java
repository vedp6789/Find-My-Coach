package com.findmycoach.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.findmycoach.app.R;

import java.util.ArrayList;

/**
 * Created by ved on 1/5/15.
 */
public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public NotificationRecyclerViewAdapter(ArrayList arrayList){


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_helper,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }



}