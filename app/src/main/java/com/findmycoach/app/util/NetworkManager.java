package com.findmycoach.app.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.findmycoach.app.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by prem on 9/1/15.
 */
public class NetworkManager {

    public static String localityName = "";
    public static String cityName = "";
    public static String stateName = "";
    public static String countryName = "";
    public static String postalCodeName = "";
    public static String countryCode="";

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                strAdd = updateVariables(addresses);
                Log.e("GPS Location Pickup", updateVariables(addresses));
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return strAdd;
    }

    public static String updateVariables(List<Address> addresses) {
        String strAdd = "";
        Address returnedAddress = addresses.get(0);
        StringBuilder strReturnedAddress = new StringBuilder("");

        for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
            if (returnedAddress.getAddressLine(i) != null && !returnedAddress.getAddressLine(i).equals("null") && !returnedAddress.getAddressLine(i).trim().equals(""))
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
        }
        strAdd = strReturnedAddress.toString();
        try {
            if (returnedAddress.getPostalCode() != null && !returnedAddress.getPostalCode().equals("null") && !returnedAddress.getPostalCode().trim().equals(""))
                postalCodeName = returnedAddress.getPostalCode();
        } catch (Exception e) {
            postalCodeName = "";
        }
        try {
            if (returnedAddress.getMaxAddressLineIndex() == 2) {
                if (returnedAddress.getAddressLine(0) != null && !returnedAddress.getAddressLine(0).equals("null") && !returnedAddress.getAddressLine(0).trim().equals(""))
                    localityName = returnedAddress.getAddressLine(0);
                Log.e("GPS Location Pickup", "Locality : " + localityName);
                if (postalCodeName == null || !postalCodeName.equals("")) {
                    getZip(returnedAddress.getAddressLine(1).trim());
                }
            } else if (returnedAddress.getMaxAddressLineIndex() == 3 || returnedAddress.getMaxAddressLineIndex() > 3) {
                if (returnedAddress.getAddressLine(0) != null && !returnedAddress.getAddressLine(0).equals("null") && !returnedAddress.getAddressLine(0).trim().equals(""))
                    localityName = returnedAddress.getAddressLine(0) + ", " + returnedAddress.getAddressLine(1);
                Log.e("GPS Location Pickup", "Locality : " + localityName);
                if (postalCodeName == null || !postalCodeName.equals("")) {
                    getZip(returnedAddress.getAddressLine(returnedAddress.getMaxAddressLineIndex() - 1).trim());
                }
            } else {
                localityName = "";
            }
        } catch (Exception e) {
            localityName = "";
        }

        try {
            cityName = returnedAddress.getLocality();
            Log.e("GPS Location Pickup", "City : " + cityName);
        } catch (Exception e) {
            cityName = "";
        }
        try {
            stateName = returnedAddress.getAdminArea();
            Log.e("GPS Location Pickup", "State : " + stateName);
        } catch (Exception e) {
            stateName = "";
        }
        try {
            countryName = returnedAddress.getCountryName();
            Log.e("GPS Location Pickup", "Country : " + countryName);
        } catch (Exception e) {
            countryName = "";
        }
        try {
            countryCode = returnedAddress.getCountryCode();
            Log.e("GPS Location Pickup", "State : " + countryCode);
        } catch (Exception e) {
            countryCode = "";
        }
        return strAdd;
    }

    public static void getZip(String returnedAddress) {
        try {
            String[] temp = returnedAddress.split(" ");
            postalCodeName = temp[temp.length - 1];
            Log.e("GPS Location Pickup", "Zip : " + postalCodeName);
        } catch (Exception e) {
            postalCodeName = "";
        }
    }

    public static void getNetworkAndGpsStatus(Context context) {
        final LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            showGpsDialog(context);
        }
        if (!isNetworkConnected(context)) {
            Toast.makeText(context, context.getResources()
                    .getString(R.string.check_network_connection), Toast.LENGTH_LONG).show();
        }
    }

    private static void showGpsDialog(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
