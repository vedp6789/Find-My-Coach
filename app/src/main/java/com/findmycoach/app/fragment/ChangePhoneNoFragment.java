package com.findmycoach.app.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.ValidatePhoneActivity;
import com.findmycoach.app.adapter.CountryCodeAdapter;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ved on 18/3/15.
 */
public class ChangePhoneNoFragment extends DialogFragment implements View.OnClickListener, Callback {

    private TextView countryCodeTV;
    private EditText phoneEditText;
    private ArrayList<String> country_code;
    private ProgressDialog progressDialog;
    private final String TAG = "FMC";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                dismiss();
                break;
            case R.id.okButton:

                final String phnNum = phoneEditText.getText().toString();

                /** If phone number is null */
                if (phnNum.equals("") || phnNum.length() < getResources().getInteger(R.integer.min_phone_length)) {
                    phoneEditText.setError(getResources().getString(R.string.enter_valid_phone_no));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            phoneEditText.setError(null);
                        }
                    }, 3500);
                }

                /** if country code is not update automatically or not selected */
                else if (countryCodeTV.getText().toString().trim().equals("Select")) {
                    countryCodeTV.setError(getResources().getString(R.string.select_country_code));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            countryCodeTV.setError(null);
                        }
                    }, 3500);
                }

                /** Phone number is provided with country code, updating user's phone number in server */
                else {
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("user_group", StorageHelper.getUserGroup(getActivity(), "user_group"));
                    Log.d(TAG, "Phone no. to get update : " + countryCodeTV.getText().toString() + "-" + phnNum);
                    requestParams.add("email", StorageHelper.getUserDetails(getActivity(), "user_email"));
                    requestParams.add("phone_number", countryCodeTV.getText().toString().trim().replace("+","") + "-" + phnNum);
                    Log.e("Change phone dialog","phone_number : " + countryCodeTV.getText().toString().trim() + "-" + phnNum);
                    progressDialog.show();
                    NetworkClient.setNewPhoneNumber(getActivity(), requestParams, this, 45);
                }
                break;
            case R.id.countryCodeTV:
                    showCountryCodeDialog();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.phone_number_dialog, container, false);
        Dialog dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);

        country_code = new ArrayList<>();
        Collections.addAll(country_code, this.getResources().getStringArray(R.array.country_codes));

        countryCodeTV = (TextView) view.findViewById(R.id.countryCodeTV);
        countryCodeTV.setText(getCountryZipCode());
        phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        countryCodeTV.setOnClickListener(this);
        view.findViewById(R.id.okButton).setOnClickListener(this);
        view.findViewById(R.id.cancelButton).setOnClickListener(this);

        return view;
    }

    /**
     * Getting country code using TelephonyManager
     */
    public String getCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "Select";
        TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        CountryID = manager.getSimCountryIso().toUpperCase();
        for (int i = 1; i < country_code.size(); i++) {
            String[] g = country_code.get(i).split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    /**
     * Dialog for selecting country code
     */
    private void showCountryCodeDialog() {
        final Dialog countryDialog = new Dialog(getActivity());
        countryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        countryDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        countryDialog.setCanceledOnTouchOutside(true);
        countryDialog.setContentView(R.layout.dialog_country_code);
        ListView listView = (ListView) countryDialog.findViewById(R.id.countryCodeListView);
        ArrayList<String> a = new ArrayList<>();
        Collections.addAll(a, getResources()
                .getStringArray(R.array.country_names));
        final ArrayList<String> b = new ArrayList<>();
        Collections.addAll(b, getResources().getStringArray(R.array.country_codes_only));
        final CountryCodeAdapter countryCodeAdapter = new CountryCodeAdapter(a, b,
                getActivity(), (EditText) countryDialog.findViewById(R.id.searchBox));
        listView.setAdapter(countryCodeAdapter);
        countryDialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countryCodeTV.setText(countryCodeAdapter.countryNameAndCode
                        .get(position).getCountryCode().replace("(", "").replace(")", ""));
                countryDialog.dismiss();
            }
        });
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        String message= (String) object;
        /*Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();*/
        try {
            JSONObject jsonObject = new JSONObject(message);
            String status = jsonObject.getString("status");
            if(status.equals("1")){
                Intent intent=new Intent(getActivity(),ValidatePhoneActivity.class);
                intent.putExtra("from","ChangePhoneNoFragment");
                startActivity(intent);
            }else{
                if(status.equals("2")){
                    Toast.makeText(getActivity(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dismiss();
/*
        if(message.equalsIgnoreCase("Successfully changed , please validate phone number to continue")){

        }
        dismiss();*/
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
