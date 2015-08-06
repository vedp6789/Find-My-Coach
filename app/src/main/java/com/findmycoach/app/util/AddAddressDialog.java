package com.findmycoach.app.util;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.category.Country;
import com.findmycoach.app.beans.student.Address;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.views.ChizzleAutoCompleteTextView;
import com.findmycoach.app.views.ChizzleEditText;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhi7 on 27/07/15.
 */
public class AddAddressDialog implements Callback {

    private Context context;
    private Dialog dialog;
    private ChizzleEditText addressLine1, zip;
    private ChizzleAutoCompleteTextView locality;
    private Spinner sp_country;
    private Button doneButton;
    private Button cancelButton;
    private AddressAddedListener mAddressAddedListener;
    private List<Country> countries;
    private ArrayList<String> country_names;
    public static int FLAG_FOR_ADD_ADDRESS_DIALOG = 0;
    private String TAG="FMC";
    private String stringLatitude = "", stringLongitude = "", nameOfLocation="";


    public AddAddressDialog(Context context) {
        this.context = context;

    }

    /**
     * Clear error from edit text when focused or on touch
     */
    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                ((TextView) v).setError(null);
        }
    };

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ((TextView) v).setError(null);
            return false;
        }
    };

    public void populateCountry(String country_code) {
        for (int i = 0; i < countries.size(); i++) {
            Country country = countries.get(i);
            String country_code_from_server = country.getIso();
            Log.e(TAG, "country code" + i + " " + country_code_from_server);
            if (country_code_from_server.equalsIgnoreCase(country_code)) {
                sp_country.setSelection(i);
            }
        }
    }

    public void updateCountryByLocation(){
        Log.e(TAG, "updateCountryByLocation method");
        if(countries != null && countries.size() > 0 ){
            String country_code = "";
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //getNetworkCountryIso
            country_code = manager.getSimCountryIso().toUpperCase();

            if(country_code != ""){
                Log.e(TAG,"country code from SIM: "+country_code);
                populateCountry(country_code);
            }else {
                country_code = NetworkManager.countryCode;
                if(country_code != ""){
                    populateCountry(country_code);
                }else{
                    GPSTracker gpsTracker = new GPSTracker(context);

                    if (gpsTracker.canGetLocation()) {
                        stringLatitude = String.valueOf(gpsTracker.latitude);
                        stringLongitude = String.valueOf(gpsTracker.longitude);
                        //nameOfLocation = ConvertPointToLocation(stringLatitude,stringLongitude);
                        new GetAddress().execute();
                    }
                }
            }


        }
    }

   /* public String ConvertPointToLocation(String Latitude, String Longitude) {
        String address = "";
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(Float.parseFloat(Latitude), Float.parseFloat(Longitude), 1);

            if (addresses.size() > 0) {
                for (int index = 0; index < addresses.get(0)
                        .getMaxAddressLineIndex(); index++)
                    address += addresses.get(0).getAddressLine(index) + " ";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }*/


    public void showPopUp() {
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_address_dialog);
        addressLine1 = (ChizzleEditText) dialog.findViewById(R.id.addressLine1);
        doneButton = (Button) dialog.findViewById(R.id.done);
        zip = (ChizzleEditText) dialog.findViewById(R.id.zip);
        cancelButton = (Button) dialog.findViewById(R.id.cancel);
        locality = (ChizzleAutoCompleteTextView) dialog.findViewById(R.id.locality);
        sp_country = (Spinner) dialog.findViewById(R.id.country);

        country_names = new ArrayList<String>();
        countries = MetaData.getCountryObject(context);

        /* Code to set country as per default Country code*/


        if (countries != null && countries.size() > 0) {
            for (int i = 0; i < countries.size(); i++) {
                Country country = countries.get(i);
                country_names.add(country.getShortName());
            }
            sp_country.setAdapter(new ArrayAdapter<String>(context, R.layout.textview, country_names));
        }

        updateCountryByLocation();

        locality.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = locality.getText().toString().trim();
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    try {

                        Address address = new Address();
                        address.setPhysicalAddress(addressLine1.getText().toString());
                        address.setLocale(locality.getText().toString());
                        address.setZip(zip.getText().toString());
                        address.setIsDefault(0);
                        if(countries != null && countries.size() > 0)
                            address.setCountryId(countries.get(sp_country.getSelectedItemPosition()).getId());

                        Log.e(TAG, address.getPhysicalAddress() + " : " + address.getLocale() + " : " + address.getZip() + " : " + address.getCountry());
                        onChildDetailsAdded(address);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }

            }

            private boolean validate() {
                boolean is_valid = true;
                if (addressLine1.getText().toString().trim().length() < 1) {
                    is_valid = false;
                    addressLine1.setError(context.getResources().getString(R.string.error_field_required));
                }
                if (locality.getText().toString().trim().length() < 1) {
                    is_valid = false;
                    locality.setError(context.getResources().getString(R.string.error_field_required));
                }
                if (zip.getText().toString().trim().length() < 1) {
                    is_valid = false;
                    zip.setError(context.getResources().getString(R.string.error_field_required));
                }
                return is_valid;
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


    }

    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key", context.getResources().getString(R.string.google_location_api_key));
        NetworkClient.autoComplete(context, requestParams, AddAddressDialog.this, 32);
    }


    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        if (object instanceof Suggestion) {
            Suggestion suggestion = (Suggestion) object;
            updateAutoSuggestion(suggestion);
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {

    }

    private void updateAutoSuggestion(Suggestion suggestion) {
        try {
            ArrayList<String> list = new ArrayList<String>();
            List<Prediction> suggestions = suggestion.getPredictions();
            for (int index = 0; index < suggestions.size(); index++) {
                list.add(suggestions.get(index).getDescription());
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(context, R.layout.textview, list);
            locality.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setAddressAddedListener(
            AddressAddedListener childDetailsAddedListener) {
        mAddressAddedListener = childDetailsAddedListener;
    }

    public void onChildDetailsAdded(Address address) {
        mAddressAddedListener.onAddressAdded(address);
    }

    public interface AddressAddedListener {
        public void onAddressAdded(Address address);
    }

    class GetAddress extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            NetworkManager.getCompleteAddressString(context,Double.parseDouble(stringLatitude),Double.parseDouble(stringLongitude));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String country_code = NetworkManager.countryCode;

                Log.e(TAG, "country code 2: " + country_code);
                if (country_code != null && country_code != "") {
                    for (int i = 0; i < countries.size(); i++) {
                        Country country = countries.get(i);
                        String country_code_from_server = country.getIso();
                        Log.e(TAG, "country code" + i + " " + country_code_from_server);
                        if (country_code_from_server.equals(country_code)) {
                            sp_country.setSelection(i);
                        }
                    }
                }
        }
    }
}


