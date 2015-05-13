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
public class ConnectionRequestFragment extends Fragment {

    public ConnectionRequestFragment(){
        Log.d("FMC","default ConnectionRequestFragment");
    }
    public static ConnectionRequestFragment newInstance(){
        Log.d("FMC","static ConnectionRequestFragment");

        ConnectionRequestFragment connectionRequestFragment=new ConnectionRequestFragment();

        return connectionRequestFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_connection_requests,container,false);
        Log.d("FMC","ConnectionRequestFragment view creation ");
        Toast.makeText(getActivity(), "ConnRequest", Toast.LENGTH_SHORT).show();

        return view;
    }
}
