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

/**
 * Created by ved on 13/5/15.
 */
public class ScheduleRequestFragment extends Fragment{

    public ScheduleRequestFragment(){
        Log.d("FMC", "default ScheduleRequestFragment");
    }

    public static ScheduleRequestFragment newInstance(){
        ScheduleRequestFragment scheduleRequestFragment=new ScheduleRequestFragment();
        Log.d("FMC","static ConnectionRequestFragment");
        return scheduleRequestFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_schedule_request,container,false);
        Toast.makeText(getActivity(),"ScheduleRequest",Toast.LENGTH_SHORT).show();
        Log.d("FMC","scheduleRequestFragment view ");
        return view;
    }
}
