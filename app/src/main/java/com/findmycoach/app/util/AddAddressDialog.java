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
import android.widget.ImageButton;
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
    private ChizzleAutoCompleteTextView locale;
    private Button doneButton;
    private Button cancelButton;
    private ImageButton deleteAddress;
    private AddressAddedListener mAddressAddedListener;
    private List<Country> countries;
    private ArrayList<String> country_names;
    public static int FLAG_FOR_ADD_ADDRESS_DIALOG = 0;
    private String TAG = "FMC";
    private String stringLatitude = "", stringLongitude = "", nameOfLocation = "";


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


    public void updateLocale() {
        Log.e(TAG, "updateCountryByLocation method");

        String locality = "";
        locality = NetworkManager.localityName;
        String state = "";
        state = NetworkManager.stateName;
        String city = "";
        city = NetworkManager.cityName;

        if (locality != "" && state != "" && city != "") {
            locale.setText(locality + ", " + city + ", " + state);
        } else {
            GPSTracker gpsTracker = new GPSTracker(context);

            if (gpsTracker.canGetLocation()) {
                stringLatitude = String.valueOf(gpsTracker.latitude);
                stringLongitude = String.valueOf(gpsTracker.longitude);
                new GetAddress().execute();
            }
        }
    }


    public void showPopUp() {
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_address_dialog);
        doneButton = (Button) dialog.findViewById(R.id.done);
        cancelButton = (Button) dialog.findViewById(R.id.cancel);
        locale = (ChizzleAutoCompleteTextView) dialog.findViewById(R.id.locale);
        deleteAddress= (ImageButton) dialog.findViewById(R.id.deleteAddressButton);

        updateLocale();
        /* Code to set country as per default Country code*/



        locale.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = locale.getText().toString().trim();
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
                        address.setCountry(0);
                        address.setCity_id(0);
                        address.setPhysical_address("");
                        address.setLocale(locale.getText().toString());
                        address.setDefault_yn(0);
                        onChildDetailsAdded(address);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }

            }

            private boolean validate() {
                boolean is_valid = true;
                if (locale.getText().toString().trim().length() < 1) {
                    is_valid = false;
                    locale.setError(context.getResources().getString(R.string.error_field_required));
                }

                return is_valid;
            }
        });

        deleteAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locale.setText("");
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
            locale.setAdapter(arrayAdapter);
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

    class GetAddress extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            NetworkManager.getCompleteAddressString(context, Double.parseDouble(stringLatitude), Double.parseDouble(stringLongitude));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String locality = "";
            locality = NetworkManager.localityName;
            String state;
            state = NetworkManager.stateName;
            String city = "";
            city = NetworkManager.cityName;

            if (locality != "" && state != "" && city != "") {
                locale.setText(locality + ", " + city + ", " + state);
            }
        }
    }
}


