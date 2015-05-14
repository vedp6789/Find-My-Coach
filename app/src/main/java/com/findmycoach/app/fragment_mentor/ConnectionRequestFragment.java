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
public class ConnectionRequestFragment extends Fragment {
    private static ArrayList<MentorNotifications> arrayList_of_connection_request;
    public ConnectionRequestFragment(){
        Log.d("FMC","default ConnectionRequestFragment");
    }
    public static ConnectionRequestFragment newInstance(ArrayList<MentorNotifications> arrayList_of_connection_request){
        Log.d("FMC","static ConnectionRequestFragment");

        Bundle args = new Bundle();
        args.putParcelableArrayList("connection_requests", arrayList_of_connection_request);

        ConnectionRequestFragment connectionRequestFragment=new ConnectionRequestFragment();
        connectionRequestFragment.setArguments(args);
        return connectionRequestFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList_of_connection_request=new ArrayList<MentorNotifications>();
        if(getArguments() != null){
            arrayList_of_connection_request=getArguments().getParcelableArrayList("connection_requests");
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_connection_requests,container,false);
        Log.d("FMC","ConnectionRequestFragment view creation ");
        Toast.makeText(getActivity(), "ConnRequest", Toast.LENGTH_SHORT).show();

        return view;
    }
}
