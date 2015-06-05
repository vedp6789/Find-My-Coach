package com.findmycoach.app.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.EditProfileActivityMentee;
import com.findmycoach.app.activity.EditProfileActivityMentor;

import java.io.IOException;
import java.util.List;

/**
 * Created by ShekharKG on 29/4/15.
 */
public class AddressFromZip extends AsyncTask<String, Void, List<Address>> {

    private Context context;
    private EditText editText;
    private String zip;

    public AddressFromZip(Context context, EditText editText) {
        this.context = context;
        this.editText = editText;
    }

    @Override
    protected List<Address> doInBackground(String... params) {

        final Geocoder geocoder = new Geocoder(context);
        try {
            zip = params[0];
            return geocoder.getFromLocationName(zip, 1);
        } catch (IOException ignored) {
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Address> addressList) {
        if (addressList != null) {
            String address = NetworkManager.updateVariables(addressList);
            if (address == null)
                return;
            Log.e("AddressFromZip", address);
            if (context instanceof DashboardActivity && !address.trim().equals("")
                    && address.trim().length() > 5) {
                try {
                    editText.setText(address.trim());
                } catch (Exception ignored) {
                }
            } else if (context instanceof EditProfileActivityMentor) {
                try {
                    address = address.replace(zip, "");
                    if (address != null && !address.equals("")
                            && editText.getText().toString().trim().equalsIgnoreCase(address.trim()))
                        editText.setText(address);
                } catch (Exception ignored) {
                }
            } else if (context instanceof EditProfileActivityMentee) {
                try {
                    address = address.replace(zip, "");
                    if (address != null && !address.equals("")
                            && editText.getText().toString().trim().equalsIgnoreCase(address.trim()))
                        editText.setText(address);
                } catch (Exception ignored) {
                }
            }
        }
    }
}
