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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    Spinner sp_country_code;
    EditText et_mobile_no;
    Button b_reset_mobile_no;
    private String[] country_code;
    private String phone_number;
    private ProgressDialog progressDialog;
    private final String TAG = "FMC";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_commit:
                if (validate()) {
                    String countryCode = sp_country_code.getSelectedItem().toString().split(",")[0].trim();
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("user_group", StorageHelper.getUserGroup(getActivity(), "user_group"));
                    Log.d(TAG, "Phone no. to get update : " + countryCode + "-" + phone_number);
                    requestParams.add("email", StorageHelper.getUserDetails(getActivity(), "user_email"));
                    requestParams.add("phone_number", countryCode + "-" + phone_number);
                    progressDialog.show();
                    NetworkClient.setNewPhoneNumber(getActivity(), requestParams, this, 45);
                }
        }
    }

    private boolean validate() {
        phone_number = et_mobile_no.getText().toString();
        if (sp_country_code.getSelectedItem().toString().contains("Select")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_country_code), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone_number.equals("")) {
            showErrorMessage(et_mobile_no, getResources().getString(R.string.error_field_required));
            return false;
        }
        if (phone_number.equals(phone_number.length() < 8)) {
            showErrorMessage(et_mobile_no, getResources().getString(R.string.enter_valid_phone_no));
            return false;
        }
        return true;
    }

    private void showErrorMessage(final EditText view, String string) {
        view.setError(string);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setError(null);
            }
        }, 3500);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_phone_no, container, false);
        sp_country_code = (Spinner) view.findViewById(R.id.sp_country_code);
        et_mobile_no = (EditText) view.findViewById(R.id.et_new_phone_no);
        b_reset_mobile_no = (Button) view.findViewById(R.id.b_commit);
        b_reset_mobile_no.setOnClickListener(this);

        country_code = getResources().getStringArray(R.array.country_codes);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, country_code);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_country_code.setAdapter(arrayAdapter);
        int index_to_show = getCountryZipCode();

        sp_country_code.setSelection(index_to_show);

        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.change_phone));
        dialog.setCanceledOnTouchOutside(true);
        return view;
    }

    public int getCountryZipCode() {
        String CountryID = "";
        TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] country_code = getActivity().getResources().getStringArray(R.array.country_codes);
        for (int i = 1; i < country_code.length; i++) {
            String[] g = country_code[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                return i;
            }
        }
        return 0;
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
