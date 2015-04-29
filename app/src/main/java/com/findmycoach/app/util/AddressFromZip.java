package com.findmycoach.app.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.findmycoach.app.fragment_mentee.HomeFragment;

import java.io.IOException;
import java.util.List;

/**
 * Created by ShekharKG on 29/4/15.
 */
public class AddressFromZip extends AsyncTask<String, Void, String> {

    private Context context;
    private EditText editText;

    public AddressFromZip(Context context, EditText editText) {
        this.context = context;
        this.editText = editText;
    }

    @Override
    protected String doInBackground(String... params) {

        final Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> add = geocoder.getFromLocationName(params[0], 1);
            if (add!= null && !add.isEmpty()) {
                Address address = add.get(0);
                // Use the address as needed
                try {
                    List<Address> addresses = geocoder.getFromLocation(address.getLatitude(), address.getLongitude(), 1);
                    Address currentAddress = addresses.get(0);

                    StringBuilder addressFromZip = new StringBuilder();
                    // Fetch the address lines using getAddressLine,
                    // join them, and send them to the thread.
                    int len = currentAddress.getMaxAddressLineIndex();

                    for (int i = 0; i < len; i++) {
                        if (currentAddress.getAddressLine(i) != null)
                            addressFromZip.append(currentAddress.getAddressLine(i));
                        if (currentAddress.getAddressLine(i) != null && i != len - 1)
                            addressFromZip.append(", ");
                    }
                    Log.e("GEOCODER", addressFromZip.toString());
                    return addressFromZip.toString();
                } catch (Exception e) {
                }

            }
        } catch (IOException e) {
            // handle exception
        }

        return null;
    }

    @Override
    protected void onPostExecute(String address) {
        if(address != null && !address.trim().equals("") && address.trim().length() > 5){
            try{
                editText.setText(address);
                HomeFragment.location_auto_suggested = address;
                HomeFragment.location_auto_suggested_temp = address;
            }catch (Exception e){

            }
        }

    }
}
