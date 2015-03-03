package com.findmycoach.app.util;

import android.content.Context;
import android.util.Log;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.beans.attachment.Attachment;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.chats.Chats;
import com.findmycoach.app.beans.requests.ConnectionRequestsResponse;
import com.findmycoach.app.beans.search.SearchResponse;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.beans.subcategory.SubCategory;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by prem on 18/12/14.
 */


    /*
    *   Different api with their unique identification value to distinguish between them.
    *       No internet                     -1
    *       login                           1
    *       register                        2
    *       forgetPassword                  3
    *       profile                         4
    *       changePassword                  5
    *       search                          6
    *       verifyMobile                    7
    *       configurations                  8
    *       myConnections                   9
    *       schedule                        10
    *       event                           11
    *       rate                            12
    *       breakConnection                 13
    *       logout                          14
    *       paymentDetails                  15
    *       payment                         16
    *       connectionRequest (post)        17
    *       respondToConnectionRequest      18
    *       notification                    19
    *       connections                     20
    *       breakConnection                 21
    *       connectionRequest (get)         22
    *       socialAuthentication            23
    *       mentorDetails                   24
    *       studentDetails                  25
    *       setPhoneNumber                  26
    *       validateOTP                     27
    *       repostOTP                       28
    *       chats                           29
    *       attachment                      30
    *       deviceRegistration              31
    *       google address api              32
    *       subCategories                   33
    *       categories                      34
    *       availableSlots                  35
    */

public class NetworkClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static final String TAG="FMC";

    public static String getAuthAbsoluteURL(String relativeUrl, Context context) {
        return context.getResources().getString(R.string.BASE_URL_WITH_AUTH) + relativeUrl;
    }

    public static String getAbsoluteURL(String relativeUrl, Context context) {
        return context.getResources().getString(R.string.BASE_URL) + relativeUrl;
    }

    public static void register(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, getAuthAbsoluteURL("register", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    Log.d(TAG,"Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG,"Success : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.successOperation(response.getMessage(), statusCode, calledApiValue);
                }catch (Exception e){
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG,"Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG,"Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void login(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(getAuthAbsoluteURL("login", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG,"Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG,"Success : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if(statusCode == 200)
                        callback.successOperation(response, statusCode, calledApiValue);
                    else
                        callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG,"Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG,"Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void registerThroughSocialMedia(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, getAuthAbsoluteURL("socialAuthentication", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG,"Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG,"Success : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if(statusCode == 200 || statusCode == 206)
                        callback.successOperation(response, statusCode, calledApiValue);
                    else
                        callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG,"Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG,"Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void updatePhoneForSocialMedia(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, getAuthAbsoluteURL("setPhoneNumber", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG,"Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG,"Success : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if(statusCode == 200)
                        callback.successOperation(response, statusCode, calledApiValue);
                    else
                        callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG,"Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG,"Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void registerGcmRegistrationId(final Context context, RequestParams requestParams,String authToken, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.post(context, getAbsoluteURL("deviceRegistration", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               try{
                   if (statusCode == 200) {
                       StorageHelper.checkGcmRegIdSentToSever(context, context.getResources().getString(R.string.reg_id_saved_to_server), true);
                   }
                   Log.d(TAG, "Success: Response Code:" + statusCode);
                   String responseJson = new String(responseBody);
                   Log.d(TAG, "Success: Response:" + responseJson);
               }catch (Exception e){
                   e.printStackTrace();
               }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                StorageHelper.checkGcmRegIdSentToSever(context,context.getResources().getString(R.string.reg_id_saved_to_server),false);
                try {
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                }
            }
        });
    }

    public static void forgetPassword(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(getAuthAbsoluteURL("forgotPassword", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    try {
                        Log.d(TAG, "Success: Response:" + new String(responseBody));
                        Log.d(TAG, "Success: Response Code:" + statusCode);
                        JSONObject jsonObject = new JSONObject(new String(responseBody));
                        if (statusCode == 200) {
                            callback.successOperation(jsonObject.get("message"), statusCode, calledApiValue);
                        } else {
                            callback.failureOperation(jsonObject.get("message"), statusCode, calledApiValue);
                        }
                    } catch (Exception e) {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG,"Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG,"Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void getProfile(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("profile", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    try {
                        String responseJson = new String(responseBody);
                        Log.d(TAG, "Success: Response:" + responseJson);
                        Log.d(TAG, "Success: Response Code:" + statusCode);

                        if (DashboardActivity.dashboardActivity.user_group == 3) {
                            Response response = new Gson().fromJson(responseJson, Response.class);
                            callback.successOperation(response, statusCode, calledApiValue);
                        }else if (DashboardActivity.dashboardActivity.user_group == 2){
                            Log.d(TAG, "getprofile");
                            ProfileResponse response = new Gson().fromJson(responseJson, ProfileResponse.class);
                            callback.successOperation(response, statusCode, calledApiValue);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseBody, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG,"Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG,"Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void updateProfile(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.post(context, getAbsoluteURL("profile", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    try {
                        String responseJson = new String(responseBody);
                        Log.d(TAG, "Success: Response:" + responseJson);
                        Log.d(TAG, "Success: Response Code:" + statusCode);
                        if (DashboardActivity.dashboardActivity.user_group == 3) {
                            Response response = new Gson().fromJson(responseJson, Response.class);
                            callback.successOperation(response, statusCode, calledApiValue);
                        }
                        else if (DashboardActivity.dashboardActivity.user_group == 2){
                            ProfileResponse response = new Gson().fromJson(responseJson, ProfileResponse.class);
                            callback.successOperation(response, statusCode, calledApiValue);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Exception: " + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG,"Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG,"Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });

    }

    public static void repostOtp(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, getAuthAbsoluteURL("repostOtp", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.successOperation(response, statusCode, calledApiValue);
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
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void verifyPhoneNumber(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, getAuthAbsoluteURL("validateOtp", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if (statusCode == 200) {
                        callback.successOperation(response, statusCode, calledApiValue);
                    } else {
                        callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                    }
                } catch (Exception e) {
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
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void autoComplete(Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        client.get(context, "https://maps.googleapis.com/maps/api/place/autocomplete/json", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    Suggestion suggestion = new Gson().fromJson(responseJson, Suggestion.class);
                    callback.successOperation(suggestion, statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public static void getConnectionRequests(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("connectionRequest", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    ConnectionRequestsResponse connectionRequestsResponse = new Gson().fromJson(responseJson,ConnectionRequestsResponse.class);
                    callback.successOperation(connectionRequestsResponse, statusCode, calledApiValue);
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
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void respondToConnectionRequest(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, getAbsoluteURL("respondToConnectionRequest", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    if (statusCode == 200)
                        callback.successOperation(null, statusCode, calledApiValue);
                    else {
                        try {
                            Response response = new Gson().fromJson(responseJson, Response.class);
                            callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                        } catch (Exception e) {
                            Log.d(TAG, "Failure: Error:" + e.getMessage());
                            callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                        }
                    }
                } catch (Exception e) {
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
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void breakConnection(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, getAbsoluteURL("breakConnection", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    if (statusCode == 200)
                        callback.successOperation(null, statusCode, calledApiValue);
                    else {
                        try {
                            Response response = new Gson().fromJson(responseJson, Response.class);
                            callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                        } catch (Exception e) {
                            Log.d(TAG, "Failure: Error:" + e.getMessage());
                            callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                        }
                    }
                } catch (Exception e) {
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
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void getAllConnectionRequest(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context, "auth_token"));
        Log.d(TAG, StorageHelper.getUserDetails(context, "auth_token"));
        client.get(context, getAbsoluteURL("connections", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    if (statusCode == 200) {
                        ConnectionRequestsResponse connectionRequestsResponse = new Gson().fromJson(responseJson, ConnectionRequestsResponse.class);
                        callback.successOperation(connectionRequestsResponse, statusCode, calledApiValue);
                    } else if (statusCode == 401) {
                        Response response = new Gson().fromJson(responseJson, Response.class);
                        callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
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
                        callback.failureOperation("Success", statusCode, calledApiValue);
                        return;
                    }
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    Log.d(TAG, "Failure: Error:" + e.getMessage());
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void sendAttachment(final Context context, final RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context, "auth_token"));
        client.post(context, getAbsoluteURL("attachment", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    try {
                        Log.d(TAG, "Success: Response Code:" + statusCode);
                        String responseJson = new String(responseBody);
                        Log.d(TAG, "Success: Response:" + responseJson);
                        Attachment attachment = new Gson().fromJson(responseJson, Attachment.class);
                        if (statusCode == 200) {
                            callback.successOperation(attachment, statusCode, calledApiValue);
                        } else {
                            callback.failureOperation(attachment.getMessage(), statusCode, calledApiValue);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.failureOperation("Unable to send attachment.", statusCode, calledApiValue);
                    }
                } catch (Exception e) {
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
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void getChatHistory(final Context context, final RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context, "auth_token"));
        client.get(context, getAbsoluteURL("chats", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Chats chats = new Gson().fromJson(responseJson, Chats.class);
                    if (statusCode == 200) {
                        callback.successOperation(chats, statusCode, calledApiValue);
                    } else {
                        callback.failureOperation(chats.getMessage(), statusCode, calledApiValue);
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
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void getStudentDetails(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("studentDetails", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
                    callback.successOperation(responseJson, statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG, "Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }


    public static void getSubCategories(Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("subCategories", context), requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try{
                            if (statusCode != 204) {
                                String responseJson = new String(responseBody);
                                Log.d(TAG, "Success: Response:" + responseJson);
                                Log.d(TAG, "Success: Response Code:" + statusCode);
                                if (statusCode == 200) {
                                    SubCategory categories = new Gson().fromJson(responseJson, SubCategory.class);
                                    callback.successOperation(categories, statusCode, calledApiValue);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                    }
                }
        );
    }

    public static void search(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("search", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    if (statusCode != 204) {
                        String responseJson = new String(responseBody);
                        Log.d(TAG, "Success: Response:" + responseJson);
                        Log.d(TAG, "Success: Response Code:" + statusCode);
                        if (statusCode == 200) {
                            callback.successOperation(responseJson, statusCode, calledApiValue);
                        } else {
                            SearchResponse searchResponse = new Gson().fromJson(responseJson, SearchResponse.class);
                            callback.failureOperation(searchResponse.getMessage(), statusCode, calledApiValue);
                        }
                    } else {
                        Log.d(TAG, "Success: Response Code:" + statusCode);
                        callback.failureOperation(context.getResources().getString(R.string.no_search_result_found), statusCode, calledApiValue);
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
                    SearchResponse searchResponse = new Gson().fromJson(responseJson, SearchResponse.class);
                    callback.failureOperation(searchResponse.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    Log.d(TAG, "Exception: " + e);
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }

            }
        });

    }

    public static void getCategories(Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("categories", context), requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try{
                            if (statusCode != 204) {
                                String responseJson = new String(responseBody);
                                Log.d(TAG, "Success: Response:" + responseJson);
                                Log.d(TAG, "Success: Response Code:" + statusCode);
                                if (statusCode == 200) {
                                    try{
                                        Category categories = new Gson().fromJson(responseJson, Category.class);
                                        callback.successOperation(categories, statusCode, calledApiValue);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                    }
                }

        );

    }

    public static void sendConnectionRequest(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.post(context, getAbsoluteURL("connectionRequest", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.successOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG, "Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson);
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    if (statusCode == 200) {
                        callback.successOperation(jsonObject.get("message"), statusCode, calledApiValue);
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }catch(Exception e){
                    e.printStackTrace();
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
                }
            }

            );
        }

    public static void getMentorDetails(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("mentorDetails", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
                    callback.successOperation(responseJson, statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG, "Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                }
            }
        });
    }

    public static void createNewSlot(final Context context, RequestParams requestParams,String auth_token, final Callback callback, final int calledApiValue) {
        if(!NetworkManager.isNetworkConnected(context)){
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection),-1,calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), auth_token);
        //requestParams.add(userGroup, "3");
        client.post(getAbsoluteURL("availableSlots", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    try {
                        Log.d(TAG, "Success: Response:" + new String(responseBody));
                        Log.d(TAG, "Success: Response Code:" + statusCode);
                        JSONObject jsonObject = new JSONObject(new String(responseBody));
                        if (statusCode == 200) {
                            callback.successOperation(jsonObject.get("message"),statusCode,calledApiValue);
                        } else {
                            callback.failureOperation(jsonObject.get("message"),statusCode,calledApiValue);
                        }
                    } catch (Exception e) {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server),statusCode,calledApiValue);
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
                    callback.failureOperation(jsonObject.get("message"),statusCode,calledApiValue);
                } catch (Exception e) {
                    callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server),statusCode,calledApiValue);
                }
            }
        });
    }
}
