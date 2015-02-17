package com.findmycoach.mentor.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.findmycoach.mentor.beans.authentication.Response;
import com.findmycoach.mentor.beans.registration.SignUpResponse;
import com.findmycoach.mentor.beans.requests.ConnectionRequestsResponse;
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

    private static final String TAG="FMC";
    public static String getAuthAbsoluteURL(String relativeUrl) {
        return BASE_URL_with_auth + relativeUrl;
    }

    public static String getAbsoluteURL(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void login(final Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add("usergroup", "3");
        client.post(getAuthAbsoluteURL("login"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d(TAG, "Success: Response:" + responseJson);
                Log.d(TAG, "Success: Response Code:" + statusCode);
                try {
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if (statusCode == 200) {
                        callback.successOperation(response);
                    } else {
                        callback.failureOperation(response.getMessage());
                    }
                } catch (Exception e) {
                    callback.failureOperation(context.getResources().getString(R.string.email_not_present_in_db));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void register(final Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, getAuthAbsoluteURL("register"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d(TAG, "Success: Response:" + responseJson);
                Log.d(TAG, "Success: Response Code:" + statusCode);
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
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    SignUpResponse response = new Gson().fromJson(responseJson, SignUpResponse.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void registerGcmRegistrationId(final Context context, RequestParams requestParams,String authToken, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.post(context, getAbsoluteURL("deviceRegistration"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d(TAG, "Success: Response:" + responseJson);
                Log.d(TAG, "Success: Response Code:" + statusCode);
                /*SignUpResponse response = new Gson().fromJson(responseJson, SignUpResponse.class);
                if (statusCode == 200) {
                    callback.successOperation(response);
                } else {
                    callback.failureOperation(response.getMessage());
                }*/

                //SignUpResponse response = new Gson().fromJson(responseJson, SignUpResponse.class);
                if (statusCode == 200) {
                    callback.successOperation(responseJson);
                } else {
                    callback.failureOperation(responseJson);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    SignUpResponse response = new Gson().fromJson(responseJson, SignUpResponse.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void forgetPassword(final Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(getAuthAbsoluteURL("forgot_password"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success: Response:" + new String(responseBody));
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    if (statusCode == 200) {
                        callback.successOperation(jsonObject.get("message"));
                    } else {
                        callback.failureOperation(jsonObject.get("message"));
                    }
                } catch (Exception e) {
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG, "Failure: Response:" + new String(responseBody));
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    callback.failureOperation(jsonObject.get("message"));
                } catch (Exception e) {
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void getProfile(final Context context, RequestParams requestParams, String authToken, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("profile"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG,"Success: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.successOperation(response);
                } catch (Exception e) {
                    Log.d(TAG, "Exception: " + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
                Log.d(TAG, "Failure: Response Code:" + statusCode);
            }
        });
    }

    public static void updateProfile(final Context context, RequestParams requestParams, String authToken, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);

        client.post(context, getAbsoluteURL("profile"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);

                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.successOperation(response);
                } catch (Exception e) {
                    Log.d(TAG, "Exception: " + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });

    }

    public static void updatePhoneForSocialMedia(final Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add("usergroup", "3");
        client.post(context, getAuthAbsoluteURL("setPhoneNumber"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d(TAG, "Success: Response:" + responseJson);
                Log.d(TAG, "Success: Response Code:" + statusCode);
                Response response = new Gson().fromJson(responseJson, Response.class);
                callback.successOperation(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void repostOtp(final Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, getAuthAbsoluteURL("repostOtp"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d(TAG, "Success: Response:" + responseJson);
                Log.d(TAG, "Success: Response Code:" + statusCode);
                Response response = new Gson().fromJson(responseJson, Response.class);
                callback.successOperation(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void registerThroughSocialMedia(final Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add("usergroup", "3");
        client.post(context, getAuthAbsoluteURL("socialAuthentication"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Success: Response:" + responseBody);
                Log.d(TAG, "Success: Response Code:" + statusCode);
                if(statusCode == 204){
                    callback.successOperation(null);
                    return;
                }
                String responseJson = new String(responseBody);
                Response response = new Gson().fromJson(responseJson, Response.class);
                if (statusCode == 200 || statusCode == 206) {
                    callback.successOperation(response);
                } else {
                    callback.failureOperation(response.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void verifyPhoneNumber(final Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, getAuthAbsoluteURL("validateOtp"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d(TAG, "Success: Response:" + responseJson);
                Log.d(TAG, "Success: Response Code:" + statusCode);
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
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void autoComplete(Context context, RequestParams requestParams, final Callback callback) {
        client.get(context, "https://maps.googleapis.com/maps/api/place/autocomplete/json", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d(TAG, "Success: Response:" + responseJson);
                Log.d(TAG, "Success: Response Code:" + statusCode);
                Suggestion suggestion = new Gson().fromJson(responseJson, Suggestion.class);
                callback.successOperation(suggestion);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public static void getConnectionRequests(final Context context, RequestParams requestParams, String authToken, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("connectionRequest"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d(TAG, "Success: Response:" + responseJson);
                Log.d(TAG, "Success: Response Code:" + statusCode);
                ConnectionRequestsResponse connectionRequestsResponse = new Gson().fromJson(responseJson,ConnectionRequestsResponse.class);
                callback.successOperation(connectionRequestsResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void respondToConnectionRequest(final Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, getAbsoluteURL("respondToConnectionRequest"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d(TAG, "Success: Response:" + responseJson);
                Log.d(TAG, "Success: Response Code:" + statusCode);
                if(statusCode == 200)
                    callback.successOperation(null);
                else{
                    try{
                        Response response = new Gson().fromJson(responseJson, Response.class);
                        callback.failureOperation(response.getMessage());
                    }catch (Exception e) {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void getAllConnectionRequest(final Context context, RequestParams requestParams, final Callback callback) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context,"auth_token"));
        client.get(context, getAbsoluteURL("connections"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseJson = new String(responseBody);
                Log.d(TAG, "Success: Response:" + responseJson);
                Log.d(TAG, "Success: Response Code:" + statusCode);
                if(statusCode == 200){
                    ConnectionRequestsResponse connectionRequestsResponse = new Gson().fromJson(responseJson,ConnectionRequestsResponse.class);
                    callback.successOperation(connectionRequestsResponse);
                }else if(statusCode == 401){
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    if(responseJson.contains("\"message\":\"Success\",")){
                        callback.failureOperation("Success");
                        return;
                    }
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }
}
