package com.findmycoach.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.findmycoach.app.R;

import java.util.List;

/**
 * Created by prem on 19/3/15.
 */
public class InterestsAdapter  extends BaseAdapter {


    private Context context;
    private List<String> list;

    public InterestsAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.area_of_interest_list_item, null);
        }
        TextView itemName = (TextView) view.findViewById(R.id.item_name);
        ImageView itemDelete = (ImageView) view.findViewById(R.id.item_delete);
        itemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(list.get(position));
                notifyDataSetChanged();
            }
        });
        itemName.setText(list.get(position));
        return view;
    }
}