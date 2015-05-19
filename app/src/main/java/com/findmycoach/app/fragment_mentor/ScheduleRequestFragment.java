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
import com.findmycoach.app.adapter.ScheduleRequestRecyclerViewAdapter;
import com.findmycoach.app.beans.UserNotifications.ScheduleRequest;
import com.findmycoach.app.util.DividerItemDecoration;
import com.findmycoach.app.util.RecyclerItemClickListener;

import java.util.ArrayList;

/**
 * Created by ved on 13/5/15.
 */
public class ScheduleRequestFragment extends Fragment {
    ArrayList<ScheduleRequest> arrayList_schedule_requests;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public ScheduleRequestFragment() {
        Log.d("FMC", "default ScheduleRequestFragment");
    }

    public static ScheduleRequestFragment newInstance(ArrayList<ScheduleRequest> arrayList_list_of_schedule_request) {
        ScheduleRequestFragment scheduleRequestFragment = new ScheduleRequestFragment();
        Log.d("FMC", "static ConnectionRequestFragment");
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("schedule_requests", arrayList_list_of_schedule_request);
        scheduleRequestFragment.setArguments(bundle);

        return scheduleRequestFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList_schedule_requests = new ArrayList<ScheduleRequest>();
        if (getArguments() != null) {
            arrayList_schedule_requests = getArguments().getParcelableArrayList("schedule_requests");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_request, container, false);

        Log.d("FMC", "scheduleRequestFragment view ");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_schedule_requests);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ScheduleRequestRecyclerViewAdapter(getActivity(), arrayList_schedule_requests);
        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (arrayList_schedule_requests.size() > 0) {
                            Intent intent = new Intent(getActivity(), MentorNotificationActions.class);
                            Bundle bundle = new Bundle();
                            ScheduleRequest scheduleRequest = arrayList_schedule_requests.get(position);
                            String status = scheduleRequest.getStatus();

                            bundle.putParcelable("schedule_req_data", scheduleRequest);
                            intent.putExtra("for", "schedule_request");
                            intent.putExtra("schedule_req_bundle", bundle);
                            if (status.equals("unread"))
                                startActivity(intent);


                        }
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
