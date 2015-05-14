package com.findmycoach.app.fragment_mentor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.ConnectionRequestRecyclerViewAdapter;
import com.findmycoach.app.beans.UserNotifications.ConnectionRequest;
import com.findmycoach.app.beans.UserNotifications.MentorNotifications;
import com.findmycoach.app.util.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ved on 13/5/15.
 */
public class ConnectionRequestFragment extends Fragment {
    private static ArrayList<ConnectionRequest> arrayList_of_connection_request;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public ConnectionRequestFragment() {
        Log.d("FMC", "default ConnectionRequestFragment");
    }


    public static ConnectionRequestFragment newInstance(ArrayList<ConnectionRequest> connectionRequests_notification) {
        Log.d("FMC", "static ConnectionRequestFragment");

        Bundle args = new Bundle();
        args.putParcelableArrayList("connection_requests", connectionRequests_notification);

        ConnectionRequestFragment connectionRequestFragment = new ConnectionRequestFragment();
        connectionRequestFragment.setArguments(args);
        return connectionRequestFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList_of_connection_request = new ArrayList<ConnectionRequest>();
        Log.d("FMC","arraylist of connection request size : "+arrayList_of_connection_request.size());
        if (getArguments() != null) {
            arrayList_of_connection_request = getArguments().getParcelableArrayList("connection_requests");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection_requests, container, false);
        Log.d("FMC", "ConnectionRequestFragment view creation ");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_connection_requests);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ConnectionRequestRecyclerViewAdapter(getActivity(), arrayList_of_connection_request);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Toast.makeText(getActivity(),"position: "+position,Toast.LENGTH_SHORT).show();
                    }
                })
        );



        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
