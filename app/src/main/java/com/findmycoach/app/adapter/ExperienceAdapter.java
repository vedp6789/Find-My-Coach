package com.findmycoach.app.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.findmycoach.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShekharKG on 11/6/15.
 */
public class ExperienceAdapter extends BaseAdapter {

    private int viewType;
    private Context context;
    private List<String> list;
    private Dialog dialog;
    private TextView outPutTextView;

    public ExperienceAdapter(int viewType, Context context, Dialog dialog, TextView outPutTextView) {
        this.viewType = viewType;
        this.context = context;
        this.dialog = dialog;
        this.outPutTextView = outPutTextView;
        addLevelOne();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.button_dob, null);
        }

        final Button button = (Button) v.findViewById(R.id.button);
        button.setTextSize(11f);
        button.setTypeface(null, Typeface.BOLD);
        button.setText(list.get(position));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (viewType) {
                    case 1:
                        addLevelTwo(list.get(position));
                        break;
                    case 2:
                        outPutTextView.setText(list.get(position));
                        dialog.dismiss();
                        break;
                }
            }
        });

        return v;
    }

    private void addLevelOne() {
        list = new ArrayList<>();
        list.add("0-10");
        list.add("11-20");
        list.add("21-30");
        list.add("31-40");
        list.add("41-50");
    }

    private void addLevelTwo(String s) {
        String[] array = s.split("-");
        int startValue = Integer.parseInt(array[0]);
        int endValue = Integer.parseInt(array[1]);

        list.clear();
        for (int i = startValue; i < endValue + 1; i++)
            list.add(String.valueOf(i));

        viewType = 2;
        notifyDataSetChanged();
    }
}
