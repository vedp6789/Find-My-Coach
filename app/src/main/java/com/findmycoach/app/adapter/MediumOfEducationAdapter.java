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
import android.widget.Toast;

import com.findmycoach.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShekharKG on 28/7/15.
 */
public class MediumOfEducationAdapter extends BaseAdapter {

    public List<String> mediumOfEducationList;
    public List<String> isSelected;
    public List<String> selectedLanguages;
    private Context context;
    private LayoutInflater layoutInflater;
    private int counter;
    private Drawable checked, unchecked;

    public MediumOfEducationAdapter(List<String> mediumOfEducationList, List<String> isSelected, Context context) {
        this.mediumOfEducationList = mediumOfEducationList;
        this.isSelected = isSelected;
        this.context = context;
        checked = context.getResources().getDrawable(R.drawable.radio_btn_selected);
        unchecked = context.getResources().getDrawable(R.drawable.radio_btn_unselected);
        layoutInflater = LayoutInflater.from(context);
        selectedLanguages = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mediumOfEducationList.size();
    }

    @Override
    public Object getItem(int position) {
        return mediumOfEducationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = layoutInflater.inflate(R.layout.qualified_coaching_single_row, null);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        textView.setText(mediumOfEducationList.get(position));
        final ImageView cb = (ImageView) v.findViewById(R.id.checkbox);

        if (isSelected.get(position).equalsIgnoreCase("true")) {
            cb.setImageDrawable(checked);
            if (!selectedLanguages.contains(mediumOfEducationList.get(position))) {
                selectedLanguages.add(mediumOfEducationList.get(position));
                counter++;
            }
            Log.e("fmc", mediumOfEducationList.get(position));
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isSelect = isSelected.get(position);
                if (isSelect.equalsIgnoreCase("true"))
                    counter--;
                else
                    counter++;

                if (counter > 4) {
                    counter--;
                    Toast.makeText(context, context.getResources().getString(R.string.max_four_medium_of_education), Toast.LENGTH_LONG).show();
                    return;
                }

                if (isSelect.equalsIgnoreCase("true")) {
                    cb.setImageDrawable(unchecked);
                    isSelected.set(position, "false");
                    selectedLanguages.remove(selectedLanguages.indexOf(mediumOfEducationList.get(position)));
                    Log.e("fmc", "false : " + mediumOfEducationList.get(position));
                } else {
                    cb.setImageDrawable(checked);
                    isSelected.set(position, "true");
                    if (!selectedLanguages.contains(mediumOfEducationList.get(position)))
                        selectedLanguages.add(mediumOfEducationList.get(position));
                    Log.e("fmc", "true : " + mediumOfEducationList.get(position));
                }
            }
        });

        return v;
    }
}
