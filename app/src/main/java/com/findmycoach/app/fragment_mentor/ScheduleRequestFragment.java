package com.findmycoach.app.fragment_mentor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.UserNotifications.MentorNotifications;
import com.findmycoach.app.beans.UserNotifications.ScheduleRequest;

import java.util.ArrayList;

/**
 * Created by ved on 13/5/15.
 */
public class ScheduleRequestFragment extends Fragment{
ArrayList<ScheduleRequest> arrayList_schedule_requests;
    public ScheduleRequestFragment(){
        Log.d("FMC", "default ScheduleRequestFragment");
    }

    public static ScheduleRequestFragment newInstance(ArrayList<ScheduleRequest> arrayList_list_of_schedule_request){
        ScheduleRequestFragment scheduleRequestFragment=new ScheduleRequestFragment();
        Log.d("FMC","static ConnectionRequestFragment");
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList("schedule_requests",arrayList_list_of_schedule_request);
        scheduleRequestFragment.setArguments(bundle);

        return scheduleRequestFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList_schedule_requests=new ArrayList<ScheduleRequest>();
        if(getArguments()  != null){
            arrayList_schedule_requests=getArguments().getParcelableArrayList("schedule_requests");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_schedule_request,container,false);

        Log.d("FMC","scheduleRequestFragment view ");
        return view;




    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(arrayList_schedule_requests.size() <=0){
            Toast.makeText(getActivity(),"No ScheduleRequest",Toast.LENGTH_SHORT).show();
        }
        
    }
}
