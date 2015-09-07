package com.findmycoach.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.MentorDetailsActivity;
import com.findmycoach.app.adapter.MentorListAdapter;
import com.findmycoach.app.beans.search.Datum;
import com.findmycoach.app.beans.search.SearchResponse;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchResultsFragment extends Fragment implements Callback {

    private int position;
    private RequestParams requestParams;
    private ListView searchResultsListView;
    private List<Datum> users;
    private MentorListAdapter mentorListAdapter;
    private String searchFor, aroundTime, searchedAroundTime;
    private Datum datum;
    private String connection_status_for_Selected_mentor, distance, charges;
    private boolean isQualified;
    private int noOfStudents;
    private static final int NEED_TO_REFRESH = 100;
    private boolean showTimeNavigation, isGettingMentorDetails;
    private TextView currentTime;
    private int timeNavigationGap;

    public static SearchResultsFragment newInstance(int position, String requestParams, String searchFor, int age, boolean showTimeNavigation, String aroundTime) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("request_params", requestParams);
        args.putString("search_for", searchFor);
        args.putInt("age_group", age);
        args.putBoolean("show_time_navigation", showTimeNavigation);
        args.putString("around_time", aroundTime);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isGettingMentorDetails = false;
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            requestParams = new RequestParams(convert(getArguments().getString("request_params")));
            searchFor = getArguments().getString("search_for");
            showTimeNavigation = getArguments().getBoolean("show_time_navigation");
            aroundTime = getArguments().getString("around_time");
            //TODO need to uncomment the below line after search api got fixed for age_group
//            requestParams.add("age_group", String.valueOf(getArguments().getInt("age_group")));
        }

        timeNavigationGap = getActivity().getResources().getInteger(R.integer.time_gap_for_search);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_results, container, false);
        searchResultsListView = (ListView) v.findViewById(R.id.user_list);
        if (NetworkManager.isNetworkConnected(getActivity())) {
            NetworkClient.search(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 6);
        }
        searchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isGettingMentorDetails) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.get_mentor_is_already_called), Toast.LENGTH_SHORT).show();
                    return;
                }
                connection_status_for_Selected_mentor = null;
                noOfStudents = 0;
                if (users != null) {
                    datum = users.get(position);
                    connection_status_for_Selected_mentor = datum.getConnectionStatus();
                    isQualified = datum.isQualified();
                    distance = String.format("%.1f", Double.parseDouble(datum.getDistance())) + " " + getActivity().getResources().getString(R.string.km);
                    try {
                        if (datum.getPrice() != null && datum.getCurrencyCode() != null) {
                            if (datum.getPriceFor().equalsIgnoreCase("cl"))
                                charges = datum.getCurrencyCode() + " " + datum.getPrice() + "/" + getActivity().getResources().getString(R.string.flexibility_in_class);
                            else
                                charges = datum.getCurrencyCode() + " " + datum.getPrice() + "/" + getActivity().getResources().getString(R.string.flexibility_in_hour);

                        }else
                            charges = "---";
                    } catch (Exception ignored) {
                        charges = "---";
                    }
                    try {
                        noOfStudents = Integer.parseInt(datum.getNumberOfStudents());
                    } catch (Exception e) {
                        noOfStudents = 0;
                    }
                    getMentorDetails(datum.getId());
                }
            }
        });

        if (showTimeNavigation) {
            v.findViewById(R.id.timeLayout).setVisibility(View.VISIBLE);
            applyListener(v);
        }

        return v;
    }

    private void applyListener(View v) {
        currentTime = (TextView) v.findViewById(R.id.currentTimeTV);
        currentTime.setText(getActivity().getResources().getString(R.string.around) + " " + aroundTime);

        v.findViewById(R.id.prevTimeIB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestParams.add("time_around", String.valueOf(getTimeInUnixTimeStamp(currentTime.getText().toString().trim(), -timeNavigationGap)));
                NetworkClient.search(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), SearchResultsFragment.this, 6);
            }
        });

        v.findViewById(R.id.nextTimeIB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestParams.add("time_around", String.valueOf(getTimeInUnixTimeStamp(currentTime.getText().toString().trim(), timeNavigationGap)));
                NetworkClient.search(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), SearchResultsFragment.this, 6);
            }
        });
    }

    private long getTimeInUnixTimeStamp(String fromTiming, int plusMinusHrs) {
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        Calendar calendar = Calendar.getInstance();
        try {
            Date dateIn24Hr = parseFormat.parse(fromTiming.split(" : ")[1].trim());
            calendar.setTime(dateIn24Hr);
            calendar.add(Calendar.HOUR_OF_DAY, plusMinusHrs);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (calendar.get(Calendar.HOUR) == 0 && calendar.get(Calendar.AM_PM) == 1)
            searchedAroundTime = "12:" + calendar.get(Calendar.MINUTE) + " PM";
        else
            searchedAroundTime = (calendar.get(Calendar.HOUR) < 10 ? ("0" + calendar.get(Calendar.HOUR)) : calendar.get(Calendar.HOUR))
                    + ":" + calendar.get(Calendar.MINUTE) + " " + (calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM");


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            Calendar c = Calendar.getInstance();
            date = format.parse(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
                    + c.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":00");
            Log.e("FMC", "Time Stamp : " + (c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
                    + c.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime() / 1000;
    }


    public static Map<String, String> convert(String str) {
        String[] tokens = str.split("=|&");
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < tokens.length - 1; ) map.put(tokens[i++], tokens[i++]);
        return map;
    }


    private void getMentorDetails(String id) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", id);
        requestParams.add("owner_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        int limit = 7;                                          //  This is a limit for getting free slots details for this mentor in terms of limit days from current date
        requestParams.add("limit", String.valueOf(limit));
        String authToken = StorageHelper.getUserDetails(getActivity(), "auth_token");
        String user_group = StorageHelper.getUserGroup(getActivity(), "user_group");
        if (user_group != null) {
            requestParams.add("user_group", StorageHelper.getUserGroup(getActivity(), "user_group"));
            NetworkClient.getMentorDetails(getActivity(), requestParams, authToken, this, 24);
            isGettingMentorDetails = true;
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.check_network_connection), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        if (calledApiValue == 24) {
            isGettingMentorDetails = false;
            Intent intent = new Intent(getActivity(), MentorDetailsActivity.class);
            intent.putExtra("mentorDetails", (String) object);
            intent.putExtra("connection_status", connection_status_for_Selected_mentor);
            intent.putExtra("no_of_students", noOfStudents);
            intent.putExtra("distance", distance );
            intent.putExtra("charges", charges);
            intent.putExtra("qualified", isQualified);
            datum = null;
            startActivityForResult(intent, NEED_TO_REFRESH);
        } else if (calledApiValue == 6) {
            SearchResponse searchResponse = new Gson().fromJson((String) object, SearchResponse.class);
            users = searchResponse.getData();
            mentorListAdapter = new MentorListAdapter(getActivity(), users, searchFor);
            searchResultsListView.setAdapter(mentorListAdapter);

            if (currentTime != null && searchedAroundTime != null)
                currentTime.setText(getActivity().getResources().getString(R.string.around) + " " + searchedAroundTime);
        }

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        if (calledApiValue == 24){
            isGettingMentorDetails = false;
            distance = "";
            charges = "";
        }
        Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
    }
}
