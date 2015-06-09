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
        final ImageView imageView = (ImageView) view.findViewById(R.id.radio);

        final SubCategoryItems item = list.get(position);
        itemName.setText(item.getItemName());

        if (item.getValue() == 1)
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.clander_next_arrow_small));
        else if (item.getValue() == 3)
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.radio_btn_selected_small));
        else if (item.getValue() == 2)
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.radio_btn_unselected_small));

        return view;
    }

    public static class SubCategoryItems {
        private String itemName;
        private int value; /*1 for has child, 2 for not selected, 3 for selected*/

        public SubCategoryItems(String itemName, int value) {
            this.itemName = itemName;
            this.value = value;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}