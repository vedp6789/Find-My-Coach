package com.findmycoach.mentor.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.mentor.activity.ChatWidgetActivity;
import com.findmycoach.mentor.adapter.ConnectionAdapter;
import com.fmc.mentor.findmycoach.R;

import java.util.ArrayList;

public class MyConnectionsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView connectionListView;

    public MyConnectionsFragment() {
        // Required empty public constructor
    }

    public static MyConnectionsFragment newInstance(String param1, String param2) {
        MyConnectionsFragment fragment = new MyConnectionsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_connections, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View rootView) {
        connectionListView = (ListView) rootView.findViewById(R.id.connectionListView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Make Network call and populate list of connection within ListView
        populateData();
    }

    private void populateData() {
        // Dummy Data
        ArrayList<String> dummyData = new ArrayList<String>();
        for(int i=0; i<25; i++)
            dummyData.add(i,"User " + (i+1) );
        ConnectionAdapter connectionAdapter = new ConnectionAdapter(getActivity(),dummyData);
        connectionListView.setAdapter(connectionAdapter);
        connectionListView.setOnItemClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent chatWidgetIntent = new Intent(getActivity(), ChatWidgetActivity.class);
        TextView textView = (TextView) view.findViewById(R.id.studentNameTV);
        chatWidgetIntent.putExtra("mentor_name", textView.getText());
        startActivity(chatWidgetIntent);
    }
}
