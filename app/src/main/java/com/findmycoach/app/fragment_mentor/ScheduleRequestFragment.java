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

import java.util.ArrayList;

/**
 * Created by ved on 13/5/15.
 */
public class ScheduleRequestFragment extends Fragment{
ArrayList<MentorNotifications> arrayList_schedule_requests;
    public ScheduleRequestFragment(){
        Log.d("FMC", "default ScheduleRequestFragment");
    }

    public static ScheduleRequestFragment newInstance(ArrayList<MentorNotifications> arrayList_list_of_schedule_request){
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
        arrayList_schedule_requests=new ArrayList<MentorNotifications>();
        if(getArguments()  != null){
            arrayList_schedule_requests=getArguments().getParcelableArrayList("schedule_requests");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_schedule_request,container,false);
        Toast.makeText(getActivity(),"ScheduleRequest",Toast.LENGTH_SHORT).show();
        Log.d("FMC","scheduleRequestFragment view ");
        return view;
    }
}
