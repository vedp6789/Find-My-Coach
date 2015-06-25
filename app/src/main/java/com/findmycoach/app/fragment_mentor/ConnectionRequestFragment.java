package com.findmycoach.app.fragment_mentor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.MentorNotificationActions;
import com.findmycoach.app.adapter.ConnectionRequestRecyclerViewAdapter;
import com.findmycoach.app.beans.UserNotifications.ConnectionRequest;
import com.findmycoach.app.util.DividerItemDecoration;
import com.findmycoach.app.util.RecyclerItemClickListener;

import java.util.ArrayList;

/**
 * Created by ved on 13/5/15.
 */
public class ConnectionRequestFragment extends Fragment {
    private static ArrayList<ConnectionRequest> arrayList_of_connection_request;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int selectedPosition = -1;

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
        Log.d("FMC", "arraylist of connection request size : " + arrayList_of_connection_request.size());
        if (getArguments() != null) {
            arrayList_of_connection_request = getArguments().getParcelableArrayList("connection_requests");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection_requests, container, false);
        Log.d("FMC", "ConnectionRequestFragment view creation ");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_connection_requests);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new ConnectionRequestRecyclerViewAdapter(getActivity(), arrayList_of_connection_request);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (arrayList_of_connection_request.size() > 0) {

                            Intent intent = new Intent(getActivity(), MentorNotificationActions.class);


                            Bundle bundle = new Bundle();
                            ConnectionRequest connectionRequest = arrayList_of_connection_request.get(position);
                            selectedPosition = position;
                            bundle.putParcelable("conn_req_data", connectionRequest);

                            intent.putExtra("for", "connection_request");
                            intent.putExtra("conn_req_bundle", bundle);
                            if (connectionRequest.getStatus().equals("unread"))
                                startActivityForResult(intent, 12345);
                        }
                    }
                })
        );


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("FMC", "in ConnectionRequestFragment onActivityResultFragment");
        if (requestCode == 12345 && resultCode == getActivity().RESULT_OK && selectedPosition != -1) {
            arrayList_of_connection_request.get(selectedPosition).setStatus("read");
            adapter = new ConnectionRequestRecyclerViewAdapter(getActivity(), arrayList_of_connection_request);
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
