package com.findmycoach.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.EditProfileActivityMentee;
import com.findmycoach.app.beans.category.Country;
import com.findmycoach.app.beans.student.ChildDetails;
import com.findmycoach.app.util.MetaData;
import com.findmycoach.app.views.ChizzleTextView;

import java.util.ArrayList;

/**
 * Created by abhi7 on 10/08/15.
 */
public class PreferredAddressAdapter extends ArrayAdapter<String> {

    private final LayoutInflater inflater;
    private int resourceId;
    private ArrayList<String> multipleAddressList;
    public int selectedIndex;

    public PreferredAddressAdapter(Context context, int resource, ArrayList<String> multipleAddressList) {
        super(context, resource);
        resourceId = resource;
        inflater = LayoutInflater.from(context);
        this.multipleAddressList = multipleAddressList;
        selectedIndex = -1;
    }

    @Override
    public int getCount() {
        return multipleAddressList.size();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.address = (RadioButton) convertView.findViewById(R.id.addressRadio);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.address.setOnCheckedChangeListener(null);

        viewHolder.address.setText(multipleAddressList.get(position));
        if (position == selectedIndex) {
            viewHolder.address.setChecked(true);

        } else
            viewHolder.address.setChecked(false);


        if (multipleAddressList.size() == 1) {
            viewHolder.address.setChecked(true);
            viewHolder.address.setEnabled(false);
        }

        viewHolder.address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });


        return convertView;
    }


    private class ViewHolder {
        private RadioButton address;
    }
}
