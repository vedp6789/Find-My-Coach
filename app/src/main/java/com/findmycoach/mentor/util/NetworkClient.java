package com.findmycoach.mentor.util;

import android.content.Context;
import android.util.Log;

import com.findmycoach.mentor.beans.authentication.AuthenticationResponse;
import com.findmycoach.mentor.beans.registration.SignUpResponse;
import com.fmc.mentor.findmycoach.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by prem on 18/12/14.
 */
public class NetworkClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String BASE_URL = "http://10.1.1.110/fmcweb/www/api/auth/";

    public static void login(Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(BASE_URL + "login", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                AuthenticationResponse response = new Gson().fromJson(responseJson, AuthenticationResponse.class);
                if (statusCode == 200) {
                    callback.successOperation(response);
                } else {
                    callback.failureOperation(response.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    AuthenticationResponse response = new Gson().fromJson(responseJson, AuthenticationResponse.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d("FMC", "Failure: Error:" + e.getMessage());
                    callback.failureOperation("Problem connecting to server");
                }
            }
        });
    }

    public static void register(Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, BASE_URL + "register", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("FMC", "Success: status code:" + statusCode);
                Log.d("FMC", "Success: headers:" + headers);
                Log.d("FMC", "Success: response:" + new String(responseBody));
                String responseJson = new String(responseBody);
                SignUpResponse response = new Gson().fromJson(responseJson, SignUpResponse.class);
                if (statusCode == 200) {
                    callback.successOperation(response);
                } else {
                    callback.failureOperation(response.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("FMC", "Failure: status code:" + statusCode);
                Log.d("FMC", "Failure: headers:" + headers);
                Log.d("FMC", "Failure: response:" + responseBody);
                Log.d("FMC", "Failure: Error:" + error.getMessage());
                try {
                    String responseJson = new String(responseBody);
                    SignUpResponse response = new Gson().fromJson(responseJson, SignUpResponse.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d("FMC", "Failure: Error:" + e.getMessage());
                    callback.failureOperation("Problem connecting to server");
                }
            }
        });
    }
}
