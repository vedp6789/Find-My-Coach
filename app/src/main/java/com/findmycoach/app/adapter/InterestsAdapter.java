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

        final SubCategoryItems items = list.get(position);
        if (items.getIsSelected() == 1)
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.radio_btn_selected_small));
        else
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.radio_btn_unselected_small));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(items.getIsSelected() == 0){
                    items.setIsSelected(1);
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.radio_btn_selected_small));
                }else if(items.getIsSelected() == 1){
                    items.setIsSelected(0);
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.radio_btn_unselected_small));
                }
            }
        });

        itemName.setText(items.getItemName());
        itemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.callOnClick();
            }
        });
        return view;
    }

    public static class SubCategoryItems {
        private String itemName;
        private int isSelected;
        private boolean isChild;

        public SubCategoryItems(String itemName, int isSelected, boolean isChild) {
            this.itemName = itemName;
            this.isSelected = isSelected;
            this.isChild = isChild;
        }

        public int getIsSelected() {
            return isSelected;
        }

        public void setIsSelected(int isSelected) {
            this.isSelected = isSelected;
        }

        public boolean isChild() {
            return isChild;
        }

        public void setIsChild(boolean isChild) {
            this.isChild = isChild;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }
    }
}