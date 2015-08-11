package com.findmycoach.app.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
    private Drawable checked, unchecked;

    public QualifiedAreaOfCoachingAdapter(List<String> selectedAreaOfCoaching, List<String> isSelected, Context context) {
        this.selectedAreaOfCoaching = selectedAreaOfCoaching;
        this.isSelected = isSelected;
        this.context = context;
        checked = context.getResources().getDrawable(R.drawable.radio_btn_selected);
        unchecked = context.getResources().getDrawable(R.drawable.radio_btn_unselected);
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
        final ImageView cb = (ImageView) v.findViewById(R.id.checkbox);

        if (isSelected.get(position).equalsIgnoreCase("true")) {
            cb.setImageDrawable(checked);
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isSelect = isSelected.get(position);
                if(isSelect.equalsIgnoreCase("true")){
                    cb.setImageDrawable(unchecked);
                    isSelected.set(position, "false");
                    Log.e("fmc", "false : " + selectedAreaOfCoaching.get(position));
                }else {
                    cb.setImageDrawable(checked);
                    isSelected.set(position, "true");
                    Log.e("fmc", "true : " + selectedAreaOfCoaching.get(position));
                }
            }
        });

        return v;
    }
}
