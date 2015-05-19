package com.findmycoach.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.findmycoach.app.R;

import java.util.List;

/**
 * Created by prem on 19/3/15.
 */
public class InterestsAdapter extends BaseAdapter {


    private Context context;
    private List<SubCategoryItems> list;

    public InterestsAdapter(Context context, List<SubCategoryItems> list) {
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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.area_of_interest_list_item, null);

        final TextView itemName = (TextView) view.findViewById(R.id.item_name);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.radio);

        final SubCategoryItems items = list.get(position);

        if(items.isSelected() == 1)
            checkBox.setChecked(true);
        else
        checkBox.setChecked(false);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    items.setSelected(1);
                else
                    items.setSelected(0);
            }
        });

        itemName.setText(items.getItemName());
        itemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        });
        return view;
    }

    public static class SubCategoryItems {
        private String itemName;
        private int isSelected;

        public SubCategoryItems(String itemName, int isSelected) {
            this.itemName = itemName;
            this.isSelected = isSelected;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int isSelected() {
            return isSelected;
        }

        public void setSelected(int isSelected) {
            this.isSelected = isSelected;
        }
    }
}