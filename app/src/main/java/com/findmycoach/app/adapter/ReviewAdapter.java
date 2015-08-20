package com.findmycoach.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.authentication.Review;

import java.util.List;

/**
 * Created by abhi7 on 06/08/15.
 */
public class ReviewAdapter extends BaseAdapter {

    private List<Review> reviews;
    private Context context;

    public ReviewAdapter(List<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.review_single_row, null);
        }
        TextView commentTV = (TextView) convertView.findViewById(R.id.commentTV);
        commentTV.setText(reviews.get(position).getComment());
        TextView commentedByTV = (TextView) convertView.findViewById(R.id.commentedByTV);
        commentedByTV.setText("(" + reviews.get(position).getCommentedBy() + ")");
        return convertView;
    }
}