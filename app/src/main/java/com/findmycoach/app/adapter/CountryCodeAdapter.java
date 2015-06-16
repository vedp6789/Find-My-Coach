package com.findmycoach.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.findmycoach.app.R;

/**
 * Created by ShekharKG on 16/6/15.
 */
public class CountryCodeAdapter extends BaseAdapter {

    private String[] countryNames;
    private String[] countryCodesOnly;
    private Context context;

    public CountryCodeAdapter(String[] countryNames, String[] countryCodesOnly, Context context) {
        this.countryNames = countryNames;
        this.countryCodesOnly = countryCodesOnly;
        this.context = context;
    }

    @Override
    public int getCount() {
        return countryNames.length;
    }

    @Override
    public Object getItem(int position) {
        return countryNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.country_code_single_row, null);
            ViewHolder holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(R.id.countryName);
            holder.code = (TextView) v.findViewById(R.id.countryCode);
            v.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) v.getTag();
        holder.name.setText(countryNames[position]);
        holder.code.setText(countryCodesOnly[position]);

        return v;
    }

    private class ViewHolder {
        private TextView name, code;
    }
}
