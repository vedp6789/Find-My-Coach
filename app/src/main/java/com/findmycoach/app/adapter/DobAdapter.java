package com.findmycoach.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.findmycoach.app.R;
import com.findmycoach.app.views.DobPicker;

import java.util.List;

/**
 * Created by ShekharKG on 11/6/15.
 */
public class DobAdapter extends BaseAdapter {

    private int viewType;
    private Context context;
    private List<String> list;
    private DobPicker dobPicker;

    public DobAdapter(int viewType, Context context, List<String> list, DobPicker dobPicker) {
        this.viewType = viewType;
        this.context = context;
        this.list = list;
        this.dobPicker = dobPicker;
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
                        dobPicker.selectedDecade = list.get(position);
                        dobPicker.showYearsInDecade();
                        break;
                    case 2:
                        dobPicker.selectedYear = list.get(position);
                        dobPicker.showMonths();
                        break;
                    case 3:
                        dobPicker.selectedMonth = list.get(position);
                        dobPicker.showDates();
                        break;
                    case 4:
                        dobPicker.selectedDay = list.get(position);
                        dobPicker.setDob();
                        break;
                }
            }
        });

        return v;
    }
}
