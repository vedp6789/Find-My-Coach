package com.findmycoach.mentor.util;

import android.content.Context;
import android.util.Log;

import com.findmycoach.mentor.beans.authentication.Response;
import com.findmycoach.mentor.beans.registration.SignUpResponse;
import com.findmycoach.mentor.beans.suggestion.Suggestion;
import com.fmc.mentor.findmycoach.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by prem on 18/12/14.
 */
public class NetworkClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String BASE_URL_with_auth = "http://demo.iglulabs.com/fmcweb/www/api/auth/";
    private static String BASE_URL = "http://demo.iglulabs.com/fmcweb/www/api/v1/";
//    private static String BASE_URL = "http://10.1.1.110/fmcweb/www/api/v1/profile/";

    public static String getAuthAbsoluteURL(String relativeUrl) {
        return BASE_URL_with_auth + relativeUrl;
    }

    public static String getAbsoluteURL(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void login(Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add("usergroup", "3");
        client.post(getAuthAbsoluteURL("login"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d("FMC", "Success: Response:" + responseJson);
                Log.d("FMC", "Success: Response Code:" + statusCode);
                try {
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if (statusCode == 200) {
                        callback.successOperation(response);
                    } else {
                        callback.failureOperation(response.getMessage());
                    }
                } catch (Exception e) {
                    callback.failureOperation("Email is not present in database.");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d("FMC", "Failure: Response:" + responseJson);
                    Log.d("FMC", "Failure: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
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
        client.post(context, getAuthAbsoluteURL("register"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d("FMC", "Success: Response:" + responseJson);
                Log.d("FMC", "Success: Response Code:" + statusCode);
                SignUpResponse response = new Gson().fromJson(responseJson, SignUpResponse.class);
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
                    Log.d("FMC", "Failure: Response:" + responseJson);
                    Log.d("FMC", "Failure: Response Code:" + statusCode);
                    SignUpResponse response = new Gson().fromJson(responseJson, SignUpResponse.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d("FMC", "Failure: Error:" + e.getMessage());
                    callback.failureOperation("Problem connecting to server");
                }
            }
        });
    }

    public static void forgetPassword(Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(getAuthAbsoluteURL("forgot_password"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d("FMC", "Success: Response:" + new String(responseBody));
                    Log.d("FMC", "Success: Response Code:" + statusCode);
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    if (statusCode == 200) {
                        callback.successOperation(jsonObject.get("message"));
                    } else {
                        callback.failureOperation(jsonObject.get("message"));
                    }
                } catch (Exception e) {
                    callback.failureOperation("Problem connection to server");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d("FMC", "Failure: Response:" + new String(responseBody));
                    Log.d("FMC", "Failure: Response Code:" + statusCode);
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    callback.failureOperation(jsonObject.get("message"));
                } catch (Exception e) {
                    callback.failureOperation("Problem connection to server");
                }
            }
        });
    }

    public static void getProfile(Context context, RequestParams requestParams, String authToken, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("profile"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d("FMC", "Success: Response:" + responseJson);
                    Log.d("FMC", "Success: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.successOperation(response);
                } catch (Exception e) {
                    Log.d("FMC", "Exception: " + e.getMessage());
                    callback.failureOperation("Problem connection to server");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    callback.failureOperation("Problem connection to server");
                }
                Log.d("FMC", "Failure: Response Code:" + statusCode);
            }
        });
    }

    public static void updateProfile(Context context, RequestParams requestParams, String authToken, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);

        client.post(context, getAbsoluteURL("profile"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d("FMC", "Success: Response:" + responseJson);
                    Log.d("FMC", "Success: Response Code:" + statusCode);

                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.successOperation(response);
                } catch (Exception e) {
                    Log.d("FMC", "Exception: " + e.getMessage());
                    callback.failureOperation("Problem connection to server");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d("FMC", "Failure: Response:" + responseJson);
                    Log.d("FMC", "Failure: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    callback.failureOperation("Problem connection to server");
                }
            }
        });

    }

    public static void registerThroughSocialMedia(Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add("usergroup", "3");
        client.post(context, getAuthAbsoluteURL("socialAuthentication"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d("FMC", "Success: Response:" + responseJson);
                Log.d("FMC", "Success: Response Code:" + statusCode);
                Response response = new Gson().fromJson(responseJson, Response.class);
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
                    Log.d("FMC", "Failure: Response:" + responseJson);
                    Log.d("FMC", "Failure: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d("FMC", "Failure: Error:" + e.getMessage());
                    callback.failureOperation("Problem connecting to server");
                }
            }
        });
    }

    public static void autoComplete(Context context, RequestParams requestParams, final Callback callback) {
        client.get(context, "https://maps.googleapis.com/maps/api/place/autocomplete/json", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d("FMC::", "Success: Response:" + responseJson);
                Log.d("FMC::", "Success: Response Code:" + statusCode);
                Suggestion suggestion = new Gson().fromJson(responseJson, Suggestion.class);
                callback.successOperation(suggestion);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }
}
