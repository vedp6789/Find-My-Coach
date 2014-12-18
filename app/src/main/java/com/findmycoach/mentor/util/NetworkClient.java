package com.findmycoach.mentor.util;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by prem on 18/12/14.
 */
public class NetworkClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String BASE_URL = "http://10.1.1.110/FMCWeb/www/mobile/register";

    public static void register(Context context, RequestParams requestParams, final Callback callback) {
        client.post(context, BASE_URL, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("FMC", "Success: status code:" + statusCode);
                Log.d("FMC", "Success: headers:" + headers);
                Log.d("FMC", "Success: response:" + responseBody);
                callback.successOperation();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("FMC", "Failure: status code:" + statusCode);
                Log.d("FMC", "Failure: headers:" + headers);
                Log.d("FMC", "Failure: response:" + responseBody);
                Log.d("FMC", "Failure: Error:" + error.getMessage());
                callback.failureOperation();
            }
        });
    }

}
