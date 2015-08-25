package com.findmycoach.app.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhi7 on 27/07/15.
 */
public class AddAddressDialog implements Callback,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,LocationListener {
    private AddAddressDialog addAddressDialog;
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

    public GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private LocationRequest mLocationRequest;


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
        addAddressDialog =this;
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setContentView(R.layout.add_address_dialog);
        doneButton = (Button) dialog.findViewById(R.id.done);
        cancelButton = (Button) dialog.findViewById(R.id.cancel);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                /*.enableAutoManage(, 0 *//* clientId *//*, this)
                */.addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000);


        locale = (ChizzleAutoCompleteTextView) dialog.findViewById(R.id.locale);
        locale.setOnItemClickListener(mAutocompleteClickListener);

        deleteAddress= (ImageButton) dialog.findViewById(R.id.deleteAddressButton);
        updateLocale();
        /* Code to set country as per default Country code*/



        /*locale.addTextChangedListener(new TextWatcher() {
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
        });*/







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

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,addAddressDialog);
                    mGoogleApiClient.disconnect();
                }
            }
        });


        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,addAddressDialog);
                    mGoogleApiClient.disconnect();
                }


            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.i(TAG,"Trying to connect with GoogleAPIclient");
                if(mGoogleApiClient != null)
                mGoogleApiClient.connect();
            }
        });

        dialog.show();



    }

    private void setBounds(Location location, int mDistanceInMeters ){
        Log.e(TAG,"inside setBounds method");
        double latRadian = Math.toRadians(location.getLatitude());

        double degLatKm = 110.574235;
        double degLongKm = 110.572833 * Math.cos(latRadian);
        double deltaLat = mDistanceInMeters / 1000.0 / degLatKm;
        double deltaLong = mDistanceInMeters / 1000.0 / degLongKm;

        double minLat = location.getLatitude() - deltaLat;
        double minLong = location.getLongitude() - deltaLong;
        double maxLat = location.getLatitude() + deltaLat;
        double maxLong = location.getLongitude() + deltaLong;

        Log.d(TAG, "Min: " + Double.toString(minLat) + "," + Double.toString(minLong));
        Log.d(TAG, "Max: " + Double.toString(maxLat) + "," + Double.toString(maxLong));

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(context, R.layout.textview,
                mGoogleApiClient, new LatLngBounds(new LatLng(minLat, minLong), new LatLng(maxLat, maxLong)), null);
        locale.setAdapter(mAdapter);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            /*Toast.makeText(getApplicationContext(), "Clicked: " + item.description,
                    Toast.LENGTH_SHORT).show();*/
            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };


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

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG,"on GoogleApIclient connected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            Log.e(TAG,"location null");

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            Log.e(TAG,"location not null");

            setBounds(location,5500);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG,"on GoogleApIclient connection suspend");

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG,"location changed");

        setBounds(location,5500);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

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


