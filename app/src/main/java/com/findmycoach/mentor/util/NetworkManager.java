package com.findmycoach.mentor.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.List;
import java.util.Locale;

/**
 * Created by prem on 9/1/15.
 */
public class NetworkManager {

    public static String getCurrentLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            Address currentAddress = addresses.get(0);
            return currentAddress.getAddressLine(0) + ", " + currentAddress.getLocality() + ", " + currentAddress.getAdminArea();
        } catch (Exception e) {
            Log.d("FMC:", "Exception: " + e.getMessage());
        }
        return "";
    }
}
