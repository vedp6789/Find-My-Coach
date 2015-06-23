package com.findmycoach.app.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.findmycoach.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShekharKG on 16/6/15.
 */
public class CountryCodeAdapter extends BaseAdapter {

    public List<CountryNameAndCode> countryNameAndCode;
    private List<CountryNameAndCode> defaultList;
    private Context context;
    private EditText searchET;
    private CountryCodeAdapter countryCodeAdapter;

    public CountryCodeAdapter(ArrayList<String> countryNames, ArrayList<String> countryCodesOnly, Context context, EditText searchET) {
        countryNameAndCode = new ArrayList<>();
        defaultList = new ArrayList<>();

        for (int i = 0; i < countryNames.size(); i++) {
            countryNameAndCode.add(new CountryNameAndCode(countryNames.get(i), countryCodesOnly.get(i)));
            defaultList.add(new CountryNameAndCode(countryNames.get(i), countryCodesOnly.get(i)));
        }

        this.context = context;
        this.searchET = searchET;
        countryCodeAdapter = this;
        applySearchableList();
    }

    private void applySearchableList() {
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                Log.d("SEARCH", searchET.getText().toString());
                String searchedWord = s.toString().toLowerCase();
                List<CountryNameAndCode> nameAndCode = new ArrayList<CountryNameAndCode>();
                for (int i = 0; i < defaultList.size(); i++) {
                    CountryNameAndCode tempCountryCodeName = defaultList.get(i);
                    if (tempCountryCodeName.getCountryName().toLowerCase().contains(searchedWord)
                            || tempCountryCodeName.getCountryCode().toLowerCase().contains(searchedWord))
                        nameAndCode.add(tempCountryCodeName);
                }

                if (nameAndCode.size() > 0)
                    countryNameAndCode = nameAndCode;
                else if (searchedWord.trim().equals(""))
                    countryNameAndCode = defaultList;
                countryCodeAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getCount() {
        return countryNameAndCode.size();
    }

    @Override
    public Object getItem(int position) {
        return countryNameAndCode.get(position);
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
        holder.name.setText(countryNameAndCode.get(position).getCountryName());
        holder.code.setText(countryNameAndCode.get(position).getCountryCode());

        return v;
    }

    private class ViewHolder {
        private TextView name, code;
    }

    public class CountryNameAndCode {

        private String countryName;
        private String countryCode;

        public CountryNameAndCode(String countryName, String countryCode) {
            this.countryName = countryName;
            this.countryCode = countryCode;
        }

        public String getCountryName() {
            return countryName;
        }

        public String getCountryCode() {
            return countryCode;
        }
    }
}
