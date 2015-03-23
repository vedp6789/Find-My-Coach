package com.findmycoach.app.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import com.findmycoach.app.activity.DashboardActivity;

import java.util.List;
import java.util.Locale;

/**
 * Created by prem on 9/1/15.
 */
public class NetworkManager {

    private static final String TAG="FMC:";
    public static int counter;

    public static String getCurrentLocation(Context context) {
        if(counter == 0 && context instanceof DashboardActivity){
            counter++;
            return "";
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            Address currentAddress = addresses.get(0);
            return currentAddress.getAddressLine(0) + ", " + currentAddress.getLocality() + ", " + currentAddress.getAdminArea();
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
                showGpsDialog(context);
        }
        return "";
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private static void showGpsDialog(final Context context) {
        final AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Do you want open GPS setting?";
        builder.setCancelable(false);
        builder.setMessage(message)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.dismiss();
                                context.startActivity(new Intent(action));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

}
