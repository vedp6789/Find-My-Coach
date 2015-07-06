package com.findmycoach.app.util;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.mentor.Currency;
import com.findmycoach.app.views.ChizzleTextView;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ShekharKG on 29/4/15.
 */
public class AddressFromZip extends AsyncTask<String, Void, String>  implements  Callback{

    private Context context;
    private EditText cityET, addressET;
    private String googleApiUrl;
    private ChizzleTextView currencySymbol=null;
    private boolean isGettingLatLng;

    public AddressFromZip(Context context, EditText cityET, EditText addressET,ChizzleTextView currencySymbol, boolean isGettingLatLng) {
        this.context = context;
        this.cityET = cityET;
        this.addressET = addressET;
        this.isGettingLatLng = isGettingLatLng;
        this.currencySymbol=currencySymbol;
        googleApiUrl = context.getResources().getString(R.string.google_api_lat_lng_from_zip);
        Log.e("AddressFromZip", "Constructor");
    }
    public AddressFromZip(Context context, EditText cityET, EditText addressET, boolean isGettingLatLng) {
        this.context = context;
        this.cityET = cityET;
        this.addressET = addressET;
        this.isGettingLatLng = isGettingLatLng;
        googleApiUrl = context.getResources().getString(R.string.google_api_lat_lng_from_zip);
        Log.e("AddressFromZip", "Constructor");
    }

    @Override
    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        String result = "";
        try {
            String url = "";

            if (isGettingLatLng) {
                url = googleApiUrl + "address=" + params[0];
            } else {
                url = googleApiUrl + "latlng=" + params[0] + "," + params[1];
            }
            Log.e("AddressFromZip", url);
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    @Override
    protected void onPostExecute(String address) {
//        Log.e("AddressFromZip", "Address is : " + address);

        try {
            JSONObject jsonObject = new JSONObject(address);
            if (jsonObject.getString("status").equalsIgnoreCase("OK")) {
                JSONObject result = jsonObject.getJSONArray("results").getJSONObject(0);
                if (isGettingLatLng) {
                    JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
                    new AddressFromZip(context, cityET, addressET,currencySymbol, false).execute(location.getString("lat"), location.getString("lng"));
                } else {
                    JSONArray address_components = result.getJSONArray("address_components");
                    String formatted_address = result.getString("formatted_address");

                    try {
                        NetworkManager.postalCodeName = address_components.getJSONObject(address_components.length() - 1).getString("long_name");
                    } catch (Exception e) {
                        NetworkManager.postalCodeName = "";
                    }

                    try {
                        NetworkManager.countryName = address_components.getJSONObject(address_components.length() - 2).getString("long_name");
                        NetworkManager.countryCode=  address_components.getJSONObject(address_components.length() - 2).getString("short_name");
                    } catch (Exception e) {
                        NetworkManager.countryName = "";
                        NetworkManager.countryCode=  "";
                    }

                    try {
                        NetworkManager.stateName = address_components.getJSONObject(address_components.length() - 3).getString("long_name");
                    } catch (Exception e) {
                        NetworkManager.stateName = "";
                    }

                    try {
                        NetworkManager.cityName = address_components.getJSONObject(3).getString("long_name");
                    } catch (Exception e) {
                        NetworkManager.cityName = "";
                    }
                    try {
                        if (NetworkManager.cityName.trim().length() > 5) {
                            String[] add = formatted_address.split(",");
                            if (add.length > 1)
                                NetworkManager.localityName = add[0];
                        }
                    } catch (Exception e) {
                        NetworkManager.localityName = "";
                    }

                    Log.e("AddressFromZip", formatted_address);
                    Log.d("AddressFromZip", NetworkManager.localityName);
                    Log.d("AddressFromZip", NetworkManager.cityName);
                    Log.d("AddressFromZip", NetworkManager.stateName);
                    Log.d("AddressFromZip", NetworkManager.countryName);
                    Log.d("AddressFromZip", NetworkManager.postalCodeName);

                    if (NetworkManager.cityName.length() > 1)
                        cityET.setText(NetworkManager.cityName);

                    if (NetworkManager.localityName.length() > 1)
                        addressET.setText(NetworkManager.localityName);

                    if(currencySymbol!=null) {
                        String authToken = StorageHelper.getUserDetails(context, "auth_token");
                        RequestParams requestParams = new RequestParams();
                        requestParams.add("country", String.valueOf(NetworkManager.countryCode));
                        NetworkClient.getCurrencySymbol(context, requestParams, authToken, this, 52);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (addressList != null) {
//            String address = NetworkManager.updateVariables(addressList);
//            if (address == null)
//                return;
//            Log.e("AddressFromZip", address);
//            if (context instanceof DashboardActivity && !address.trim().equals("")
//                    && address.trim().length() > 5) {
//                try {
//                    editText.setText(address.trim());
//                } catch (Exception ignored) {
//                }
//            } else if (context instanceof EditProfileActivityMentor) {
//                try {
//                    address = address.replace(zip, "");
//                    if (address != null && !address.equals("")
//                            && !editText.getText().toString().trim().equalsIgnoreCase(address.trim()))
//                        editText.setText(address);
//                } catch (Exception ignored) {
//                }
//            } else if (context instanceof EditProfileActivityMentee) {
//                try {
//                    address = address.replace(zip, "");
//                    if (address != null && !address.equals("")
//                            && !editText.getText().toString().trim().equalsIgnoreCase(address.trim()))
//                        editText.setText(address);
//                } catch (Exception ignored) {
//                }
//            }
//        }
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        Currency currency=(Currency)object;
        currencySymbol.setText(Html.fromHtml(currency.getCurrencySymbol()));

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {

    }
}
