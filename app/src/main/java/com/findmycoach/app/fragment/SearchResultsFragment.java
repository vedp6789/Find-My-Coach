package com.findmycoach.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchResultsFragment extends Fragment implements Callback {

    private int position;
    private RequestParams requestParams;
    private ListView searchResultsListView;
    private List<Datum> users;
    private MentorListAdapter mentorListAdapter;
    private String searchFor;
    private Datum datum;
    private String connection_status_for_Selected_mentor;
    private static final int NEED_TO_REFRESH = 100;

    public static SearchResultsFragment newInstance(int position, String requestParams, String searchFor, int age) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("request_params", requestParams);
        args.putString("search_for", searchFor);
        args.putInt("age_group", age);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            requestParams = new RequestParams(convert(getArguments().getString("request_params")));
            searchFor = getArguments().getString("search_for");
            //TODO need to uncomment the below line after search api got fixed for age_group
//            requestParams.add("age_group", String.valueOf(getArguments().getInt("age_group")));
        }
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
                connection_status_for_Selected_mentor = null;
                if (users != null) {
                    datum = users.get(position);
                    connection_status_for_Selected_mentor = datum.getConnectionStatus();
                    getMentorDetails(datum.getId());
                }
            }
        });
        return v;
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
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.check_network_connection), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        if (calledApiValue == 24) {
            Intent intent = new Intent(getActivity(), MentorDetailsActivity.class);
            intent.putExtra("mentorDetails", (String) object);
            intent.putExtra("connection_status", connection_status_for_Selected_mentor);
            datum = null;
            startActivityForResult(intent, NEED_TO_REFRESH);
        } else {
            SearchResponse searchResponse = new Gson().fromJson((String) object, SearchResponse.class);
            users = searchResponse.getData();
            mentorListAdapter = new MentorListAdapter(getActivity(), users, searchFor);
            searchResultsListView.setAdapter(mentorListAdapter);
        }

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {

    }
}
