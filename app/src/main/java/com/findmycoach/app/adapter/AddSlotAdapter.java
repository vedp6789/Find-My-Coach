package com.findmycoach.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.AddNewSlotActivity;

/**
 * Created by prem on 26/5/15.
 */
public class AddSlotAdapter extends BaseAdapter {

    private String[] days;
    private AddNewSlotActivity context;

    public AddSlotAdapter(AddNewSlotActivity context, String[] days) {
        this.days = days;
        this.context = context;
    }

    @Override
    public int getCount() {
        return days.length;
    }

    @Override
    public Object getItem(int position) {
        return days[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.screen_gridcell, null);
        final Button button = (Button) v.findViewById(R.id.calendar_day_gridcell);
        button.setTextSize(11f);
        button.setTypeface(null, Typeface.BOLD);
        button.setText(days[position]);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0:
                        context.boo_mon_checked = !context.boo_mon_checked;
                        updateView(button, context.boo_mon_checked);
                        break;
                    case 1:
                        context.boo_tue_checked = !context.boo_tue_checked;
                        updateView(button, context.boo_tue_checked);
                        break;
                    case 2:
                        context.boo_wed_checked = !context.boo_wed_checked;
                        updateView(button, context.boo_wed_checked);
                        break;
                    case 3:
                        context.boo_thurs_checked = !context.boo_thurs_checked;
                        updateView(button, context.boo_thurs_checked);
                        break;
                    case 4:
                        context.boo_fri_checked = !context.boo_fri_checked;
                        updateView(button, context.boo_fri_checked);
                        break;
                    case 5:
                        context.boo_sat_checked = !context.boo_sat_checked;
                        updateView(button, context.boo_sat_checked);
                        break;
                    case 6:
                        context.boo_sun_checked = !context.boo_sun_checked;
                        updateView(button, context.boo_sun_checked);
                        break;
                }
            }
        });
        return v;
    }

    private void updateView(Button btn, boolean isChecked){
        if(isChecked)
            btn.setBackground(context.getResources().getDrawable(R.drawable.scheduled_event_arrow));
        else
            btn.setBackgroundColor(context.getResources().getColor(R.color.white));
    }
}
