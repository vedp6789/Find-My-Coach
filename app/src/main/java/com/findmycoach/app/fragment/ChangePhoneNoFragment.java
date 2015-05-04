package com.findmycoach.app.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

/**
 * Created by ved on 18/3/15.
 */
public class ChangePhoneNoFragment extends DialogFragment implements View.OnClickListener, Callback {

    private TextView countryCodeTV;
    private EditText phoneEditText;
    private String[] country_code;
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
                if (phnNum.equals("") || phnNum.length() < 8) {
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
                    requestParams.add("phone_number", countryCodeTV.getText().toString() + "-" + phnNum);
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
        dialog.setTitle(getString(R.string.change_phone));
        dialog.setCanceledOnTouchOutside(true);

        country_code = this.getResources().getStringArray(R.array.country_codes);

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
        for (int i = 1; i < country_code.length; i++) {
            String[] g = country_code[i].split(",");
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
        countryDialog.setCanceledOnTouchOutside(true);
        countryDialog.setTitle(getResources().getString(R.string.select_country_code));
        countryDialog.setContentView(R.layout.dialog_country_code);
        ListView listView = (ListView) countryDialog.findViewById(R.id.countryCodeListView);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, country_code));
        countryDialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countryCodeTV.setText(country_code[position].split(",")[0]);
                countryDialog.dismiss();
            }
        });
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
