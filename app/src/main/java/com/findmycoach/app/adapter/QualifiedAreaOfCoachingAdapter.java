package com.findmycoach.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.findmycoach.app.R;

import java.util.List;

/**
 * Created by ShekharKG on 28/7/15.
 */
public class QualifiedAreaOfCoachingAdapter extends BaseAdapter {

    public List<String> selectedAreaOfCoaching;
    public List<String> isSelected;
    private Context context;
    private LayoutInflater layoutInflater;

    public QualifiedAreaOfCoachingAdapter(List<String> selectedAreaOfCoaching, List<String> isSelected, Context context) {
        this.selectedAreaOfCoaching = selectedAreaOfCoaching;
        this.isSelected = isSelected;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return selectedAreaOfCoaching.size();
    }

    @Override
    public Object getItem(int position) {
        return selectedAreaOfCoaching.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = layoutInflater.inflate(R.layout.qualified_coaching_single_row, null);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        textView.setText(selectedAreaOfCoaching.get(position));
        final CheckBox cb = (CheckBox) v.findViewById(R.id.checkbox);
        if (isSelected.get(position).equalsIgnoreCase("true"))
            cb.setChecked(true);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb.setChecked(!cb.isChecked());
                isSelected.set(position, cb.isChecked() + "");
                Log.e("fmc", cb.isChecked() + " : " + selectedAreaOfCoaching.get(position));
            }
        });

        return v;
    }
}
