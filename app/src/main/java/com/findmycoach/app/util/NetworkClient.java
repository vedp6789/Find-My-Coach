package com.findmycoach.app.util;

import android.content.Context;
import android.util.Log;

import com.findmycoach.app.beans.attachment.Attachment;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.beans.chats.Chats;
import com.findmycoach.app.beans.registration.SignUpResponse;
import com.findmycoach.app.beans.requests.ConnectionRequestsResponse;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.R;
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

    private static final String TAG="FMC";
    private static final String userGroup = "user_group";

    public static String getAuthAbsoluteURL(String relativeUrl, Context context) {
        return context.getResources().getString(R.string.BASE_URL_WITH_AUTH) + relativeUrl;
    }

    public static String getAbsoluteURL(String relativeUrl, Context context) {
        return context.getResources().getString(R.string.BASE_URL) + relativeUrl;
    }

    public static void login(final Context context, RequestParams requestParams, final Callback callback) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        //requestParams.add(userGroup, "3");
        client.post(getAuthAbsoluteURL("login", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
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
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
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
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        //requestParams.add(userGroup, "3");
        client.post(context, getAuthAbsoluteURL("register", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    SignUpResponse response = new Gson().fromJson(responseJson, SignUpResponse.class);
                    if (statusCode == 200) {
                        callback.successOperation(response);
                    } else {
                        callback.failureOperation(response.getMessage());
                    }
                }catch (Exception e){
                    e.printStackTrace();
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
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        //requestParams.add(userGroup, "3");
        client.post(context, getAbsoluteURL("deviceRegistration", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               try{
                   String responseJson = new String(responseBody);
                   Log.d(TAG, "Success: Response:" + responseJson);
                   Log.d(TAG, "Success: Response Code:" + statusCode);

                   if (statusCode == 200) {
                       StorageHelper.checkGcmRegIdSentToSever(context,context.getResources().getString(R.string.reg_id_saved_to_server),true);
                       //callback.successOperation(response);
                   } else {
                       // callback.failureOperation(response.getMessage());
                   }
//
//                if (statusCode == 200) {
//                    callback.successOperation(responseJson);
//                } else {
//                    callback.failureOperation(responseJson);
//                }
               }catch (Exception e){
                   e.printStackTrace();
               }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                StorageHelper.checkGcmRegIdSentToSever(context,context.getResources().getString(R.string.reg_id_saved_to_server),false);
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    SignUpResponse response = new Gson().fromJson(responseJson, SignUpResponse.class);
                    callback.failureOperation(response.getMessage());
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                }
            }
        });
    }

    public static void forgetPassword(final Context context, RequestParams requestParams, final Callback callback) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(userGroup, "3");
        client.post(getAuthAbsoluteURL("forgot_password", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
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
                }catch (Exception e){
                    e.printStackTrace();
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
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(userGroup, "3");
        client.get(context, getAbsoluteURL("profile", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
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
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage());
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                } catch (Exception e) {
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void updateProfile(final Context context, RequestParams requestParams, String authToken, final Callback callback) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(userGroup, "3");
        client.post(context, getAbsoluteURL("profile", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
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
                }catch (Exception e){
                    e.printStackTrace();
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
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(userGroup, "3");
        client.post(context, getAuthAbsoluteURL("setPhoneNumber", context), requestParams, new AsyncHttpResponseHandler() {
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
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(userGroup, "3");
        client.post(context, getAuthAbsoluteURL("repostOtp", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.successOperation(response);
                }catch (Exception e){
                    e.printStackTrace();
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

    public static void registerThroughSocialMedia(final Context context, RequestParams requestParams, final Callback callback) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(userGroup, "3");
        client.post(context, getAuthAbsoluteURL("socialAuthentication", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    Log.d(TAG, "Success: Response:" + responseBody);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    if(statusCode == 204){
                        callback.successOperation(null);
                        return;
                    }
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if (statusCode == 200 || statusCode == 206) {
                        callback.successOperation(response);
                    } else {
                        callback.failureOperation(response.getMessage());
                    }
                }catch (Exception e){
                    e.printStackTrace();
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
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(userGroup, "3");
        client.post(context, getAuthAbsoluteURL("validateOtp", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if (statusCode == 200) {
                        callback.successOperation(response);
                    } else {
                        callback.failureOperation(response.getMessage());
                    }
                }catch (Exception e){
                    e.printStackTrace();
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
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    Suggestion suggestion = new Gson().fromJson(responseJson, Suggestion.class);
                    callback.successOperation(suggestion);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public static void getConnectionRequests(final Context context, RequestParams requestParams, String authToken, final Callback callback) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(userGroup, "3");
        client.get(context, getAbsoluteURL("connectionRequest", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    ConnectionRequestsResponse connectionRequestsResponse = new Gson().fromJson(responseJson,ConnectionRequestsResponse.class);
                    callback.successOperation(connectionRequestsResponse);
                }catch (Exception e){
                    e.printStackTrace();
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

    public static void respondToConnectionRequest(final Context context, RequestParams requestParams, final Callback callback) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(userGroup, "3");
        client.post(context, getAbsoluteURL("respondToConnectionRequest", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    if (statusCode == 200)
                        callback.successOperation(null);
                    else {
                        try {
                            Response response = new Gson().fromJson(responseJson, Response.class);
                            callback.failureOperation(response.getMessage());
                        } catch (Exception e) {
                            Log.d(TAG, "Failure: Error:" + e.getMessage());
                            callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
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

    public static void breakConnection(final Context context, RequestParams requestParams, final Callback callback) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(userGroup, "3");
        client.post(context, getAbsoluteURL("breakConnection", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    if (statusCode == 200)
                        callback.successOperation(null);
                    else {
                        try {
                            Response response = new Gson().fromJson(responseJson, Response.class);
                            callback.failureOperation(response.getMessage());
                        } catch (Exception e) {
                            Log.d(TAG, "Failure: Error:" + e.getMessage());
                            callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
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
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context, "auth_token"));
        Log.d(TAG, StorageHelper.getUserDetails(context, "auth_token"));
        requestParams.add(userGroup, "3");
        client.get(context, getAbsoluteURL("connections", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    if (statusCode == 200) {
                        ConnectionRequestsResponse connectionRequestsResponse = new Gson().fromJson(responseJson, ConnectionRequestsResponse.class);
                        callback.successOperation(connectionRequestsResponse);
                    } else if (statusCode == 401) {
                        Response response = new Gson().fromJson(responseJson, Response.class);
                        callback.failureOperation(response.getMessage());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    if (responseJson.contains("\"message\":\"Success\",")) {
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

    public static void sendAttachment(final Context context, final RequestParams requestParams, final Callback callback) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context, "auth_token"));
        requestParams.add(userGroup, "3");
        client.post(context, getAbsoluteURL("attachment", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    try{
                        Log.d(TAG, "Success: Response Code:" + statusCode);
                        String responseJson = new String(responseBody);
                        Log.d(TAG, "Success: Response:" + responseJson);
                        Attachment attachment = new Gson().fromJson(responseJson, Attachment.class);
                        if (statusCode == 200) {
                            callback.successOperation(attachment);
                        } else {
                            callback.failureOperation(attachment.getMessage());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        callback.failureOperation("Unable to send attachment.");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    Log.d(TAG, "Failure: Response:" + new String(responseBody));

                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void getChatHistory(final Context context, final RequestParams requestParams, final Callback callback) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context, "auth_token"));
        requestParams.add(userGroup, "3");
        client.get(context, getAbsoluteURL("chats", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Chats chats = new Gson().fromJson(responseJson, Chats.class);
                    if (statusCode == 200) {
                        callback.successOperation(chats);
                    } else {
                        callback.failureOperation(chats.getMessage());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG, "Failure: Response:" + new String(responseBody));
                    Log.d(TAG, "Failure: Response Code:" + statusCode);

                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
            }
        });
    }

    public static void getStudentDetails(final Context context, RequestParams requestParams, String authToken, final Callback callback) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection));
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(userGroup, "2");
        client.get(context, getAbsoluteURL("studentDetails", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    if (responseBody != null) {
                        String responseJson = new String(responseBody);
                        Log.d(TAG, "Success: Response:" + responseJson);
                        ProfileResponse profileResponse = new Gson().fromJson(responseJson, ProfileResponse.class);
                        if(statusCode == 200){
                            callback.successOperation(responseJson);
                        }else{
                            callback.failureOperation(profileResponse.getMessage());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);

                }catch (Exception e){
                    Log.d(TAG, "Exception: " + e);
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server));
                }
//                try {
//                    com.findmycoach.student.beans.mentor.Response response = new Gson().fromJson(responseJson, com.findmycoach.student.beans.mentor.Response.class);
//                    callback.failureOperation(response.getMessage());
//                } catch (Exception e) {
//                    callback.failureOperation(context.getResources().getString(R.string.problem_in_server_connection));
//                }
            }
        });
    }
}
