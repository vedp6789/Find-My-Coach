package com.findmycoach.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.student.Address;
import com.findmycoach.app.beans.student.ChildDetails;

import java.util.ArrayList;

/**
 * Created by abhi7 on 27/07/15.
 */
public class AddressAdapter extends ArrayAdapter<ChildDetails> {

    private Context context;
    private ArrayList<Address> addressArrayList;
    private LayoutInflater inflater;
    private int resourceId;

    public AddressAdapter(Context context, int resource, ArrayList<Address> addressArrayList) {
        super(context, resource);
        this.context = context;
        resourceId = resource;
        inflater = LayoutInflater.from(context);
        this.addressArrayList = addressArrayList;

    }

    @Override
    public int getCount() {
        return addressArrayList.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.address = (TextView) convertView.findViewById(android.R.id.text1);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.address.setText(addressArrayList.get(position).getAddressLine1()+" "+ addressArrayList.get(position).getLocality()+" "+addressArrayList.get(position).getZip());
        return convertView;
    }


    private class ViewHolder {
        private TextView address;


    }
}

