package com.findmycoach.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.EditProfileActivityMentee;
import com.findmycoach.app.beans.category.Country;
import com.findmycoach.app.beans.student.Address;
import com.findmycoach.app.beans.student.ChildDetails;
import com.findmycoach.app.util.MetaData;
import com.findmycoach.app.views.ChizzleTextView;

import java.util.ArrayList;
import java.util.List;


public class AddressAdapter extends ArrayAdapter<ChildDetails> {

    private Context context;
    private ArrayList<Address> addressArrayList;
    private LayoutInflater inflater;
    private int resourceId;
    private List<Country> countries;
    private ArrayList<String> country_names;
    private ListView addressListView;

    public AddressAdapter(Context context, int resource, ArrayList<Address> addressArrayList,ListView addressListView) {
        super(context, resource);
        this.context = context;
        resourceId = resource;
        inflater = LayoutInflater.from(context);
        this.addressArrayList = addressArrayList;
        country_names = new ArrayList<String>();
        countries = new ArrayList<Country>();
        countries = MetaData.getCountryObject(context);
        this.addressListView=addressListView;


    }

    @Override
    public int getCount() {
        return addressArrayList.size();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.address = (ChizzleTextView) convertView.findViewById(R.id.address);
            viewHolder.deleteButton = (ImageButton) convertView.findViewById(R.id.deleteAddressButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int id = addressArrayList.get(position).getCountryId();
        String country_name = "";
        if (countries != null && countries.size() > 0) {
            for (int i = 0; i < countries.size(); i++) {
                Country country = countries.get(i);
                int country_id = country.getId();
                if (id == country_id) {
                    country_name = country.getShortName();
                }
            }
        }

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressArrayList.remove(position);
                notifyDataSetChanged();
                EditProfileActivityMentee.setListViewHeightBasedOnChildren(addressListView);
            }
        });


        viewHolder.address.setText(addressArrayList.get(position).getPhysicalAddress().trim() + ", " + addressArrayList.get(position).getLocale().trim() + ", " + addressArrayList.get(position).getZip().trim());
        return convertView;
    }


    private class ViewHolder {
        private ChizzleTextView address;
        private ImageButton deleteButton;
    }
}

