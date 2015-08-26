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
    public static String countryCode = "";
    private static String TAG="FMC";

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        getNetworkAndGpsStatus(context);
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
            if (returnedAddress.getAddressLine(i) != null && !returnedAddress.getAddressLine(i).equals("null") && !returnedAddress.getAddressLine(i).trim().isEmpty())
                strReturnedAddress.append(", ").append(returnedAddress.getAddressLine(i));
        }
        strAdd = strReturnedAddress.toString().replaceFirst(", ", "");

        Log.e(TAG,"GPS address 1 :::::"+strAdd);

        try {
            if (returnedAddress.getPostalCode() != null && !returnedAddress.getPostalCode().equals("null") && !returnedAddress.getPostalCode().trim().isEmpty())
                postalCodeName = returnedAddress.getPostalCode();
        } catch (Exception e) {
            postalCodeName = "";
        }
        try {
            if (returnedAddress.getMaxAddressLineIndex() == 2) {
                if (returnedAddress.getAddressLine(0) != null && !returnedAddress.getAddressLine(0).equals("null") && !returnedAddress.getAddressLine(0).trim().isEmpty())
                    localityName = returnedAddress.getAddressLine(0);
                Log.e("GPS Location Pickup", "Locality : " + localityName);
                if (postalCodeName == null || !postalCodeName.equals("")) {
                    getZip(returnedAddress.getAddressLine(1).trim());
                }
            } else if (returnedAddress.getMaxAddressLineIndex() == 3 || returnedAddress.getMaxAddressLineIndex() > 3) {
                if (returnedAddress.getAddressLine(0) != null && !returnedAddress.getAddressLine(0).equals("null") && !returnedAddress.getAddressLine(0).trim().isEmpty()){
                    String add = returnedAddress.getAddressLine(1);
                    if(add != null && !add.isEmpty() && !add.equals("null")){
                        localityName = returnedAddress.getAddressLine(0) + ", " + returnedAddress.getAddressLine(1);
                    }else{
                        localityName = returnedAddress.getAddressLine(0);

                    }

                }
                Log.e("GPS Location Pickup", "Locality : " + localityName);
                if (postalCodeName == null || !postalCodeName.equals("") ) {
                    getZip(returnedAddress.getAddressLine(returnedAddress.getMaxAddressLineIndex() - 1).trim());
                }
            } else {
                localityName = "";
            }
        } catch (Exception e) {
            localityName = "";
        }

        if(localityName != null && !localityName.equals("null") && !localityName.trim().isEmpty()){
            if(strAdd != null && !strAdd.contains(localityName)){
                if(!strAdd.trim().isEmpty()){
                    strReturnedAddress.append(", "+localityName);
                    strAdd=strReturnedAddress.toString().replaceFirst(", ", "");
                }else{
                    strReturnedAddress.append(localityName);
                    strAdd=strReturnedAddress.toString().replaceFirst(", ", "");
                }
            }
        }


        try {

            if(returnedAddress.getLocality() != null && !returnedAddress.getLocality().equals("null") && !returnedAddress.getLocality().trim().isEmpty()){
                cityName = returnedAddress.getLocality();
                Log.e("GPS Location Pickup", "City : " + cityName);
            }
        } catch (Exception e) {
            cityName = "";
        }

        if(cityName != null && !cityName.equals("null") && !cityName.trim().isEmpty()){
            if(strAdd != null && !strAdd.contains(cityName) ){

                if(!strAdd.trim().isEmpty()){
                    strReturnedAddress.append(", "+cityName);
                    strAdd=strReturnedAddress.toString().replaceFirst(", ", "");
                }else {
                    strReturnedAddress.append(cityName);
                    strAdd=strReturnedAddress.toString().replaceFirst(", ", "");
                }

            }
        }



        try {
            if(returnedAddress.getAdminArea() != null && !returnedAddress.getAdminArea().equals("null") && !returnedAddress.getAdminArea().trim().isEmpty()) {
                stateName = returnedAddress.getAdminArea();
                Log.e("GPS Location Pickup", "State : " + stateName);
            }
        } catch (Exception e) {
            stateName = "";
        }


        if(stateName != null && !stateName.equals("null") && !stateName.trim().isEmpty()){
            if(strAdd != null && !strAdd.contains(stateName) ){

                if(!strAdd.trim().isEmpty()){
                    strReturnedAddress.append(", "+stateName);
                    strAdd=strReturnedAddress.toString().replaceFirst(", ", "");
                }else {
                    strReturnedAddress.append(stateName);
                    strAdd=strReturnedAddress.toString().replaceFirst(", ", "");
                }

            }
        }
        try {
            if(returnedAddress.getCountryName() != null && !returnedAddress.getCountryName().equals("null") && !returnedAddress.getCountryName().trim().isEmpty()) {
                countryName = returnedAddress.getCountryName();
                Log.e("GPS Location Pickup", "Country : " + countryName);
            }
        } catch (Exception e) {
            countryName = "";
        }


        if(countryName != null && !countryName.equals("null") && !countryName.trim().isEmpty()){
            if(strAdd != null && !strAdd.contains(countryName) ){

                if(!strAdd.trim().isEmpty()){
                    strReturnedAddress.append(", "+countryName);
                    strAdd=strReturnedAddress.toString().replaceFirst(", ", "");
                }else {
                    strReturnedAddress.append(countryName);
                    strAdd=strReturnedAddress.toString().replaceFirst(", ", "");
                }

            }
        }


        try {
            if(returnedAddress.getCountryCode() != null && !returnedAddress.getCountryCode().equals("null") && !returnedAddress.getCountryCode().trim().isEmpty()) {
                countryCode = returnedAddress.getCountryCode();
                Log.e("GPS Location Pickup", "State : " + countryCode);
            }
        } catch (Exception e) {
            countryCode = "";
        }


        if(postalCodeName != null && !postalCodeName.equals("null") && !postalCodeName.trim().isEmpty()){
            if(strAdd != null && !strAdd.contains(postalCodeName) ){

                if(!strAdd.trim().isEmpty()){
                    strReturnedAddress.append(", "+postalCodeName);
                    strAdd=strReturnedAddress.toString().replaceFirst(", ", "");
                }else {
                    strReturnedAddress.append(postalCodeName);
                    strAdd=strReturnedAddress.toString().replaceFirst(", ", "");
                }

            }
        }

Log.e(TAG,"GPS address:::::"+strAdd);


        return strAdd;
    }

    public static void getZip(String returnedAddress) {
        try {
            String[] temp = returnedAddress.split(" ");
            if (temp[temp.length -1] != null && !temp[temp.length -1].equals("null") && !temp[temp.length -1].trim().isEmpty())
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
