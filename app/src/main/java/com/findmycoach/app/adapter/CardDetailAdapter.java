package com.findmycoach.app.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.findmycoach.app.R;

import org.json.JSONArray;
import org.w3c.dom.Text;

/**
 * Created by ved on 23/3/15.
 */
public class CardDetailAdapter extends BaseAdapter{
    Context context;
    JSONArray jsonArray_card_details;
    ProgressDialog progressDialog;
    TextView tv_card_type,tv_card_number;
    public CardDetailAdapter(Context context,JSONArray jsonArray,ProgressDialog progressDialog){
        this.context=context;
        jsonArray_card_details=jsonArray;
        this.progressDialog=progressDialog;
    }




    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.card_detail, null);
        }
        tv_card_type= (TextView) view.findViewById(R.id.tv_card_type);
        tv_card_number= (TextView) view.findViewById(R.id.tv_card_number);

        return view;
    }
}
