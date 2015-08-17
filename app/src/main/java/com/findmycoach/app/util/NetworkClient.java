package com.findmycoach.app.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.CityDetails;
import com.findmycoach.app.beans.Promotions.Promotions;
import com.findmycoach.app.beans.attachment.Attachment;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.chats.Chats;
import com.findmycoach.app.beans.mentor.Currency;
import com.findmycoach.app.beans.requests.ConnectionRequestsResponse;
import com.findmycoach.app.beans.search.SearchResponse;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
    *       breakConnection                 13  // not used anywhere for this use api 21
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
    *       exceptions                      36
    *       calenderDetails  3 months        37
    *       calenderDetails  next month      38
    *       calenderDetails  previous month  39
    *       calenderDetails  3months mentee            40
    *       calenderDetails  next month menteec        41
    *       calenderDetails  prev month mentee         42
    *       calenderEvents                  43
    *       resetPassword                   44
    *       setPhoneNumber                  45     // To change Phone number
    *       event                           46     // used when Mentee go for all details validation for a schedule
    *       savePayPalConsent               47     // used to get save PayPal consent details on server
    *       payMentor                       48
    *       eventFinalize                   49
    *       exceptions                      50     // deleteVacation
    *       availableSlots                  51     // deleteClassSlot
    *       currencySymbol                  52
    *       saveCardDetails                 53     //Save entered card details
    *       city                            54
    *       saveCardDetails                 53
    *       //Save entered card details
    *   CountryConfig                       55
    *   Grades                              56
    *   MediumOfEducation                   57
    *   promotion (get all )                          58
    *   promotion (add/update)                              59
    *   promoton(delete)                              60
    *   
    * */


public class NetworkClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static final String TAG = "FMC";
    public static String timeZone, language;
    private static Context context;
    private static String API_KEY;
    private static String API_KEY_VALUE;
    private static String AUTH_TOKEN;
    private static String AUTH_TOKEN_VALUE;
    public static Callback callback;
    public static int calledAPIValue;

    private static String getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
        String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset + ":00";
        Log.e(TAG, "TimeZone : " + offset);
        return offset;
    }

    private static String getSystemLanguage() {
        return Locale.getDefault().getDisplayLanguage();
    }


    public static String getAuthAbsoluteURL(String relativeUrl, Context context) {
        if (timeZone == null || timeZone.equals(""))
            timeZone = getTimeZone();

        if (language == null || language.equals(""))
            language = getSystemLanguage();

        return context.getResources().getString(R.string.BASE_URL_WITH_AUTH) + relativeUrl;
    }

    public static String getAbsoluteURL(String relativeUrl, Context context) {
        if (timeZone == null || timeZone.equals(""))
            timeZone = getTimeZone();

        if (language == null || language.equals(""))
            language = getSystemLanguage();
        return context.getResources().getString(R.string.BASE_URL) + relativeUrl;
    }

    public static void register(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);

        client.post(context, getAuthAbsoluteURL("register", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.successOperation(response, statusCode, calledApiValue);
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
                    try {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void cities(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);


        client.get(context, getAuthAbsoluteURL("city", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    ArrayList<CityDetails> cityDetailsArrayList = new ArrayList<>();
                    Log.d(TAG, "Success : Status code 766: " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response 766: " + responseJson);
                    JSONObject jsonObject = new JSONObject(responseJson);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    cityDetailsArrayList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<CityDetails>>() {
                    }.getType());
                    callback.successOperation(cityDetailsArrayList, statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG, "Failure : Status code city api: " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response: " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void login(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(getAuthAbsoluteURL("login", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if (statusCode == 200) {
                        StorageHelper.saveUserProfile(context, responseJson);
                        callback.successOperation(response, statusCode, calledApiValue);
                    } else
                        callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
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
                    try {
                        e.printStackTrace();
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void registerThroughSocialMedia(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAuthAbsoluteURL("socialAuthentication", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if (statusCode == 200 || statusCode == 206) {
                        StorageHelper.saveUserProfile(context, responseJson);
                        callback.successOperation(response, statusCode, calledApiValue);
                    } else
                        callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
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
                    try {
                        e.printStackTrace();
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void updatePhoneForSocialMedia(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAuthAbsoluteURL("setPhoneNumber", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if (statusCode == 200)
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
                    Log.d(TAG, "Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        e.printStackTrace();
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void registerGcmRegistrationId(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.post(context, getAbsoluteURL("deviceRegistration", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (statusCode == 200) {
                        StorageHelper.checkGcmRegIdSentToSever(context, context.getResources().getString(R.string.reg_id_saved_to_server), true);
                    }
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                StorageHelper.checkGcmRegIdSentToSever(context, context.getResources().getString(R.string.reg_id_saved_to_server), false);
                try {
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response:" + responseJson);
                } catch (Exception e) {
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void getAllPromotions(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("promotion", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);

                    /*ArrayList<Promotion> promotionsArrayList = new ArrayList<>();
                    Log.d(TAG, "Success : Status code promotion get all: " + statusCode);
                    JSONObject jsonObject=new JSONObject(responseJson);
                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                    promotionsArrayList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<Promoti>>(){}.getType());
                    callback.successOperation(cityDetailsArrayList, statusCode, calledApiValue);*/

                    Promotions promotions = new Gson().fromJson(responseJson, Promotions.class);
                    callback.successOperation(promotions, statusCode, calledApiValue);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                StorageHelper.checkGcmRegIdSentToSever(context, context.getResources().getString(R.string.reg_id_saved_to_server), false);
                try {
                    Log.d(TAG, "Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void addPromotion(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.post(context, getAbsoluteURL("promotion", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);

                    /*ArrayList<Promotion> promotionsArrayList = new ArrayList<>();
                    Log.d(TAG, "Success : Status code promotion get all: " + statusCode);
                    JSONObject jsonObject=new JSONObject(responseJson);
                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                    promotionsArrayList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<Promoti>>(){}.getType());
                    callback.successOperation(cityDetailsArrayList, statusCode, calledApiValue);*/

                    JSONObject jsonObject = new JSONObject(responseJson);
                    callback.successOperation(jsonObject, statusCode, calledApiValue);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                StorageHelper.checkGcmRegIdSentToSever(context, context.getResources().getString(R.string.reg_id_saved_to_server), false);
                try {
                    Log.d(TAG, "Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void deletePromotion(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);

        Header[] headers = {
                new BasicHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value))
                , new BasicHeader(context.getResources().getString(R.string.auth_key), authToken),
                new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded")
        };

        client.delete(context, getAbsoluteURL("promotion", context), headers, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);

                    /*ArrayList<Promotion> promotionsArrayList = new ArrayList<>();
                    Log.d(TAG, "Success : Status code promotion get all: " + statusCode);
                    JSONObject jsonObject=new JSONObject(responseJson);
                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                    promotionsArrayList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<Promoti>>(){}.getType());
                    callback.successOperation(cityDetailsArrayList, statusCode, calledApiValue);*/

                    JSONObject jsonObject = new JSONObject(responseJson);
                    callback.successOperation(jsonObject, statusCode, calledApiValue);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                StorageHelper.checkGcmRegIdSentToSever(context, context.getResources().getString(R.string.reg_id_saved_to_server), false);
                try {
                    Log.d(TAG, "Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void forgetPassword(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
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
                    Log.d(TAG, "Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void getProfile(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("profile", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    try {
                        String responseJson = new String(responseBody);
                        Log.d(TAG, "Success: Response:" + responseJson);
                        Log.d(TAG, "Success: Response Code:" + statusCode);
                        int user_group = Integer.parseInt(StorageHelper.getUserGroup(context, "user_group"));
                        if (user_group == 3) {
                            Response response = new Gson().fromJson(responseJson, Response.class);
                            callback.successOperation(response, statusCode, calledApiValue);
                        } else if (user_group == 2) {
                            Log.d(TAG, "getprofile");
                            ProfileResponse response = new Gson().fromJson(responseJson, ProfileResponse.class);
                            callback.successOperation(response, statusCode, calledApiValue);
                        }
                        if (statusCode == 200)
                            StorageHelper.saveUserProfile(context, responseJson);
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
                    Log.d(TAG, "Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void updateProfile(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("profile", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    try {
                        String responseJson = new String(responseBody);
                        Log.d(TAG, "Success: Response:" + responseJson);
                        Log.d(TAG, "Success: Response Code:" + statusCode);
                        int userGroup = Integer.parseInt(StorageHelper.getUserGroup(context, "user_group"));
                        if (userGroup == 3) {
                            Response response = new Gson().fromJson(responseJson, Response.class);
                            callback.successOperation(response, statusCode, calledApiValue);
                        } else if (userGroup == 2) {
                            ProfileResponse response = new Gson().fromJson(responseJson, ProfileResponse.class);
                            callback.successOperation(response, statusCode, calledApiValue);
                        }
                        if (statusCode == 200)
                            StorageHelper.saveUserProfile(context, responseJson);
                    } catch (Exception e) {
                        Log.d(TAG, "Exception: " + e);
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                    try {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });

    }

    public static void repostOtp(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAuthAbsoluteURL("repostOtp", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.successOperation(response, statusCode, calledApiValue);
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void verifyPhoneNumber(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAuthAbsoluteURL("validateOtp", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if (statusCode == 200) {
                        StorageHelper.saveUserProfile(context, responseJson);
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void autoComplete(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
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
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.get(context, getAbsoluteURL("connectionRequest", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    ConnectionRequestsResponse connectionRequestsResponse = new Gson().fromJson(responseJson, ConnectionRequestsResponse.class);
                    callback.successOperation(connectionRequestsResponse, statusCode, calledApiValue);
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void respondToConnectionRequest(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("respondToConnectionRequest", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    if (statusCode == 200)
                        callback.successOperation(responseJson, statusCode, calledApiValue);
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void breakConnection(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("breakConnection", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    if (statusCode == 200) {
                        callback.successOperation("Success", statusCode, calledApiValue);
                    } else {
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void getAllConnectionRequest(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context, "auth_token"));
        Log.d(TAG, StorageHelper.getUserDetails(context, "auth_token"));
        client.get(context, getAbsoluteURL("connections", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
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
                    if (responseJson.contains("\"message\":\"Success\",") || responseJson.contains("No connections found")) {
                        callback.failureOperation("Success", statusCode, calledApiValue);
                        return;
                    }
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void sendAttachment(final Context context, final RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context, "auth_token"));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void getChatHistory(final Context context, final RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context, "auth_token"));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.get(context, getAbsoluteURL("chats", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success: Response Code:" + statusCode);
                    Log.d(TAG, "Success: Response:" + responseJson);
                    printLongLog(responseJson);
                    Chats chats = new Gson().fromJson(responseJson, Chats.class);
                    if (statusCode == 200) {
                        callback.successOperation(chats, statusCode, calledApiValue);
                    } else {
                        callback.failureOperation(chats.getMessage(), statusCode, calledApiValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG, "Failure: Response:" + new String(responseBody));
                    Log.d(TAG, "Failure: Response Code:" + statusCode);

                } catch (Exception e) {
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void getStudentDetails(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void getSubCategories(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.get(context, getAuthAbsoluteURL("metaData", context), requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            if (statusCode != 204) {
                                String responseJson = new String(responseBody);
                                Log.d(TAG, "Success: Response: on splash metaData call " + responseJson);
                                Log.d(TAG, "Success: Response Code:" + statusCode);
                                if (statusCode == 200) {
                                    try {
                                        callback.successOperation(responseJson, statusCode, calledApiValue);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        callback.failureOperation("", statusCode, calledApiValue);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        try {
                            Log.d(TAG, "Success: Response:" + statusCode);
                            callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                        } catch (Exception ignored) {
                        }
                    }
                }

        );
    }

    public static void search(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        Log.e(TAG, "Request params Search : " + requestParams.toString());
        client.get(context, getAbsoluteURL("search", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
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
                    SearchResponse searchResponse = new Gson().fromJson(responseJson, SearchResponse.class);
                    callback.failureOperation(searchResponse.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }

            }
        });

    }

    public static void getCategories(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("categorySubCategory", context), requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            if (statusCode != 204) {
                                String responseJson = new String(responseBody);
                                Log.d(TAG, "Success: Response:" + responseJson);
                                Log.d(TAG, "Success: Response Code:" + statusCode);
                                if (statusCode == 200) {
                                    try {
                                        Category categories = new Gson().fromJson(responseJson, Category.class);
                                        callback.successOperation(categories, statusCode, calledApiValue);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        try {
                            Log.d(TAG, "Failure: Response:" + statusCode);
                            callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                        } catch (Exception ignored) {
                        }
                    }
                }

        );

    }

    public static void sendConnectionRequest(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("connectionRequest", context), requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            Log.d(TAG, "Success : Status code : " + statusCode);
                            String responseJson = new String(responseBody);
                            Log.d(TAG, "Success : Response : " + responseJson);
                            JSONObject jsonObject = new JSONObject(new String(responseBody));
                            callback.successOperation(jsonObject.get("message"), statusCode, calledApiValue);
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                        } catch (Exception e) {
                            try {
                                Log.d(TAG, "Failure: Error:" + e.getMessage());
                                callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }

        );
    }

    public static void getMentorDetails(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        Log.e(TAG, requestParams.toString());
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void createNewSlot(final Context context, RequestParams requestParams, String auth_token, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), auth_token);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        Log.e(TAG, timeZone + " : " + language);
        //requestParams.add(userGroup, "3");
        client.post(getAbsoluteURL("availableSlots", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    try {
                        Log.d(TAG, "Success: Response:" + new String(responseBody));
                        Log.d(TAG, "Success: Response Code:" + statusCode);

                        if (statusCode == 200) {
                            callback.successOperation(new String(responseBody), statusCode, calledApiValue);
                        } else {
                            callback.failureOperation(new String(responseBody), statusCode, calledApiValue);
                        }
                    } catch (Exception e) {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG, "Failure: Response:" + new String(responseBody));
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);

                } catch (Exception e) {

                    try {
                        Log.d(TAG, " inside onFailure catch for createNewSlot method ");
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {

                    }
                }
            }
        });
    }

    public static void scheduleVacation(final Context context, RequestParams requestParams, String auth_token, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), auth_token);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        //requestParams.add(userGroup, "3");
        client.post(getAbsoluteURL("exceptions", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    try {
                        Log.d(TAG, "Success: Response:" + new String(responseBody));
                        Log.d(TAG, "Success: Response Code:" + statusCode);
                        JSONObject jsonObject = new JSONObject(new String(responseBody));
                        if (statusCode == 200) {
                            callback.successOperation(jsonObject.toString(), statusCode, calledApiValue);
                        } else {
                            callback.failureOperation(jsonObject.toString(), statusCode, calledApiValue);
                        }
                    } catch (Exception e) {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG, "Failure: Response:" + new String(responseBody));
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    callback.failureOperation(new String(responseBody), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        Log.d(TAG, " inside onFailure catch for schedule vaccation method");
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void getCalendarDetails(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        // client.get(context, getAbsoluteURL("calenderDetails", context), requestParams, new AsyncHttpResponseHandler() {
        client.get(context, getAbsoluteURL("calenderDetails", context), requestParams, new AsyncHttpResponseHandler() {
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void getMenteeCalendarDetails(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        // client.get(context, getAbsoluteURL("calenderDetails", context), requestParams, new AsyncHttpResponseHandler() {
        client.get(context, getAbsoluteURL("calenderDetailsMentee", context), requestParams, new AsyncHttpResponseHandler() {
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void deleteClassSlot(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        // Header[] headers = {new BasicHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value)), new BasicHeader(context.getResources().getString(R.string.auth_key), authToken)};

        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("slotsDelete", context), requestParams, new AsyncHttpResponseHandler() {
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });


    }


    public static void deleteVacation(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("exceptionsDelete", context), requestParams, new AsyncHttpResponseHandler() {
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });

    }


    public static void getCalenderEvent(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        // client.get(context, getAbsoluteURL("calenderDetails", context), requestParams, new AsyncHttpResponseHandler() {
        client.get(context, getAbsoluteURL("calenderEvents", context), requestParams, new AsyncHttpResponseHandler() {
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void resetPassword(final Context context, RequestParams requestParams, String auth_token, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), auth_token);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        //requestParams.add(userGroup, "3");
        client.post(getAuthAbsoluteURL("resetPassword", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    try {
                        Log.d(TAG, "Success: Response:" + new String(responseBody));
                        Log.d(TAG, "Success: Response Code:" + statusCode);
                        JSONObject jsonObject = new JSONObject(new String(responseBody));
                        if (statusCode == 200) {
                            callback.successOperation(jsonObject.getString("message"), statusCode, calledApiValue);
                        } else {
                            callback.failureOperation(jsonObject.getString("message"), statusCode, calledApiValue);
                        }
                    } catch (Exception e) {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG, "Failure: Response:" + new String(responseBody));
                    Log.d(TAG, "Failure: Response Code:" + statusCode);
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    callback.failureOperation(jsonObject.get("message"), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void setNewPhoneNumber(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context, context.getString(R.string.auth_token)));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAuthAbsoluteURL("setPhoneNumber", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    if (statusCode == 200)
                        callback.successOperation(jsonObject.toString(), statusCode, calledApiValue);
                    else
                        callback.failureOperation(jsonObject.getString("message"), statusCode, calledApiValue);
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void rateMentor(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        Log.d("API", getAuthAbsoluteURL("mentorRating", context));
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("mentorRating", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
                    if (statusCode == 200)
                        callback.successOperation("Success", statusCode, calledApiValue);
                    else {
                        Response response = new Gson().fromJson(responseJson, Response.class);
                        callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                    }
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void validateMenteeEvent(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("event", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if (statusCode == 200)
                        callback.successOperation(new String(responseBody), statusCode, calledApiValue);
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
                    Log.d(TAG, "Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson);
                    callback.failureOperation(new String(responseBody), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void postScheduleRequest(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context, context.getString(R.string.auth_token)));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("event", context), requestParams, new AsyncHttpResponseHandler() {
//            client.post(context, getAbsoluteURL("event", context), requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if (statusCode == 200)
                        callback.successOperation(response.getMessage(), statusCode, calledApiValue);
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
                    Log.d(TAG, "Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson);
                    callback.failureOperation(new JSONObject(new String(responseBody)).get("message"), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void saveCardDetails(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("getCardDetails", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
//                    Response response = new Gson().fromJson(responseJson, Response.class);
//                    if(statusCode == 200)
//                        callback.successOperation(response, statusCode, calledApiValue);
//                    else
//                        callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void savePayPalConsent(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("getCardDetails", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
//                    Response response = new Gson().fromJson(responseJson, Response.class);
//                    if(statusCode == 200)
//                        callback.successOperation(response, statusCode, calledApiValue);
//                    else
//                        callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void payMentor(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(context, getAbsoluteURL("getCardDetails", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
//                    Response response = new Gson().fromJson(responseJson, Response.class);
//                    if(statusCode == 200)
//                        callback.successOperation(response, statusCode, calledApiValue);
//                    else
//                        callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
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
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void finalizeEvent(final Context context, RequestParams requestParams, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), StorageHelper.getUserDetails(context, context.getString(R.string.auth_token)));
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.post(getAbsoluteURL("eventFinalize", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    if (statusCode == 200)
                        callback.successOperation(responseJson, statusCode, calledApiValue);
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
                    Log.d(TAG, "Failure : Status code : " + statusCode);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage());
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void getUserNotifications(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        Log.d(TAG, "time zone: " + timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.get(context, getAbsoluteURL("notification", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d(TAG, "Success : Status code : " + statusCode + "\ncalled_api+" + calledApiValue);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Success : Response : " + responseJson + "\ncalled_api+" + calledApiValue);
                    callback.successOperation(responseJson, statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d(TAG, "Failure : Status code : " + statusCode + "\ncalled_api+" + calledApiValue);
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : Response : " + responseJson + "\ncalled_api+" + calledApiValue);
                    Response response = new Gson().fromJson(responseJson, Response.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        Log.d(TAG, "Failure: Error:" + e.getMessage() + "\ncalled_api+" + calledApiValue);
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void getCurrencySymbol(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        requestParams.add(context.getResources().getString(R.string.time_zone), timeZone);
        requestParams.add(context.getResources().getString(R.string.device_language), language);
        client.get(context, getAbsoluteURL("currencySymbol", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d("CurrencyNew", responseJson);

                    Currency response = new Gson().fromJson(responseJson, Currency.class);
                    callback.successOperation(response, statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d("CurrencyNew", responseJson);

                    Currency response = new Gson().fromJson(responseJson, Currency.class);
                    callback.failureOperation(response.getMessage(), statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void getGrades(final Context context, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("grades", context), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    callback.successOperation(responseJson, statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d("CurrencyNew", responseJson);
                    callback.failureOperation(responseJson, statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public static void getCountryConfig(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("countryConfig", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    callback.successOperation(responseJson, statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d("CurrencyNew", responseJson);
                    callback.failureOperation(responseJson, statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    public static void getMediumOfEducation(final Context context, RequestParams requestParams, String authToken, final Callback callback, final int calledApiValue) {
        if (!NetworkManager.isNetworkConnected(context)) {
            callback.failureOperation(context.getResources().getString(R.string.check_network_connection), -1, calledApiValue);
            return;
        }
        client.addHeader(context.getResources().getString(R.string.api_key), context.getResources().getString(R.string.api_key_value));
        client.addHeader(context.getResources().getString(R.string.auth_key), authToken);
        client.get(context, getAbsoluteURL("mediumOfEducation", context), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseJson = new String(responseBody);
                    Log.e(TAG, "Success : " + responseJson);
                    callback.successOperation(responseJson, statusCode, calledApiValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseBody, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseJson = new String(responseBody);
                    Log.d(TAG, "Failure : " + responseJson);
                    callback.failureOperation(responseJson, statusCode, calledApiValue);
                } catch (Exception e) {
                    try {
                        callback.failureOperation(context.getResources().getString(R.string.problem_in_connection_server), statusCode, calledApiValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    private static void printLongLog(String veryLongString) {
        int maxLogSize = 1000;
        for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > veryLongString.length() ? veryLongString.length() : end;
            Log.v(TAG, veryLongString.substring(start, end));
        }
    }

    //   NetworkClient.deletePromotionHttpClient(getActivity(),StorageHelper.getUserGroup(getActivity(), "auth_token"),jsonArray,this,60);
    public static void deletePromotionHttpClient(Context context, String auth_token, JSONArray jsonArray, Callback callback, int called_api) throws JSONException {
        Log.e(TAG, "deletePromotionsHttpClient");
        jsonArray.put(new JSONObject().put(context.getResources().getString(R.string.time_zone), timeZone));
        jsonArray.put(new JSONObject().put(context.getResources().getString(R.string.device_language), language));
        DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask();
        ArrayList<String> stringArrayList = new ArrayList<>();

        stringArrayList.add(String.valueOf(jsonArray));  /* data to communicate*/
        stringArrayList.add(getAbsoluteURL("promotion", context));  /* URL*/

        NetworkClient.context = context;
        NetworkClient.API_KEY = context.getResources().getString(R.string.api_key);
        API_KEY_VALUE = context.getResources().getString(R.string.api_key_value);
        AUTH_TOKEN = context.getResources().getString(R.string.auth_key);
        AUTH_TOKEN_VALUE = auth_token;
        NetworkClient.callback = callback;
        calledAPIValue = called_api;

        deleteAsyncTask.execute(stringArrayList);


    }


    static class DeleteAsyncTask extends AsyncTask<ArrayList<String>, Void, String> {

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            try {

                ArrayList<String> stringArrayList = new ArrayList<>();
                stringArrayList = params[0];
                for (String s : stringArrayList) {
                    Log.e(TAG, "string in doInBackground: " + s);
                }
                URL url_http1 = null;
                if (stringArrayList.size() > 0) {
                    url_http1 = new URL(stringArrayList.get(1));
                }
                if (url_http1 != null) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url_http1.openConnection();
                    //httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("DELETE");
                    // httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    if (NetworkClient.API_KEY != null && NetworkClient.API_KEY_VALUE != null) {
                        httpURLConnection.setRequestProperty(NetworkClient.API_KEY, NetworkClient.API_KEY_VALUE);
                    }
                    if (NetworkClient.AUTH_TOKEN != null && NetworkClient.AUTH_TOKEN_VALUE != null) {
                        httpURLConnection.setRequestProperty(NetworkClient.AUTH_TOKEN, NetworkClient.AUTH_TOKEN_VALUE);
                    }

                    httpURLConnection.setRequestProperty("DATA", stringArrayList.get(0));
                    httpURLConnection.connect();

/*


                    OutputStream os = httpURLConnection.getOutputStream();
                    String sending_data_to_server = stringArrayList.get(0);

                    os.write(sending_data_to_server.getBytes());
                    os.close();
                    os.flush();
*/

                    BufferedReader br = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));


                    String sr = "";
                    System.out.println("Output from Server .... \n");
                    String line = "";

                    while ((line = br.readLine()) != null)
                        sr += line;
                                   /* while ((sr = br.readLine()) != null) {
                                        sr=br.readLine();
                                        System.out.println("Response:"+sr);
                                    }*/
                    Log.e(TAG, "Validation response from server:" + sr);
                    //Integer ip = Integer.parseInt(sr);
                    System.out.println("Output from Server \n" + sr);
                    httpURLConnection.disconnect();

                    return sr;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {

            } else {
                Log.e(TAG, "Problem connecting to server!");
            }

        }
    }

}



