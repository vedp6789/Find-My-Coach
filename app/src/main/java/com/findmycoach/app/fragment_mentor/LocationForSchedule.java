package com.findmycoach.app.fragment_mentor;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.fragment.MyScheduleFragment;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ved on 24/3/15.
 */
public class LocationForSchedule extends DialogFragment implements Callback{
    AutoCompleteTextView auto_tv_location;
    Button b_ok,b_cancel;
    public String TAG="FMC";
    public MyScheduleFragment myScheduleFragment;
    private String location=null;
    private String last_location_selected=null;
    ArrayAdapter<String> arrayAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_auto_type_address,container,false);
        auto_tv_location= (AutoCompleteTextView) view.findViewById(R.id.auto_tv_location);
        b_ok= (Button) view.findViewById(R.id.b_ok);
        b_cancel= (Button) view.findViewById(R.id.b_cancel);
        /*auto_tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(location != null){
                    Log.d(TAG,"Location : "+location);

                }else{
                    auto_tv_location.setText(null);
                    Log.d(TAG,"Location : "+ location +" doesn't match from suggested address ");

                }
            }
        });*/


        auto_tv_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                location = arrayAdapter.getItem(position).toString();
                last_location_selected=location;

            }
        });

        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.calendar_by_loc));
        dialog.setCanceledOnTouchOutside(false);
        launchListener();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"LocationForSchedule dialog is on destroy");
    }

    private void launchListener() {
        auto_tv_location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                location=null;    /* making location string null because if user do changes in location and doest not select location from suggested location then this location string should be null which is used to validate the location */
                if(location != null){
                    Log.d(TAG,"Location after text changed  : "+location);

                }else{
                    Log.d(TAG,"Location after text changed : "+" not set correctly yet ");

                }
                String input = auto_tv_location.getText().toString();
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
                }
            }
        });

        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(location != null){
                    if (location.equalsIgnoreCase(auto_tv_location.getText().toString())){
                        myScheduleFragment.calendar_by_location=auto_tv_location.getText().toString();
                        dismiss();
                        myScheduleFragment.getCalendarDetailsAPICall();
                    }else{
                        if(last_location_selected.equalsIgnoreCase(auto_tv_location.getText().toString())){
                            myScheduleFragment.calendar_by_location=auto_tv_location.getText().toString();
                            dismiss();
                            myScheduleFragment.getCalendarDetailsAPICall();
                        }else{
                            Toast.makeText(getActivity(),getResources().getString(R.string.select_suggested_location),Toast.LENGTH_SHORT).show();
                        }

                    }
                }else{
                    if( last_location_selected != null && last_location_selected.equalsIgnoreCase(auto_tv_location.getText().toString())){
                        myScheduleFragment.calendar_by_location=auto_tv_location.getText().toString();
                        dismiss();
                        myScheduleFragment.getCalendarDetailsAPICall();
                    }else{
                        Toast.makeText(getActivity(),getResources().getString(R.string.select_suggested_location),Toast.LENGTH_SHORT).show();
                    }

                   // Toast.makeText(getActivity(),getResources().getString(R.string.select_suggested_location),Toast.LENGTH_SHORT).show();
                }



            }
        });

        b_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                myScheduleFragment.getCalendarDetailsAPICall();
            }
        });
    }

    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key", getResources().getString(R.string.google_location_api_key));
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.autoComplete(getActivity(), requestParams, this, 32);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        if (object instanceof Suggestion) {
            Suggestion suggestion = (Suggestion) object;
            updateAutoSuggestion(suggestion);
        }
    }


    private void updateAutoSuggestion(Suggestion suggestion) {
        ArrayList<String> list = new ArrayList<String>();
        List<Prediction> suggestions = suggestion.getPredictions();
        for (int index = 0; index < suggestions.size(); index++) {
            list.add(suggestions.get(index).getDescription());
        }
        arrayAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list);
        auto_tv_location.setAdapter(arrayAdapter);
        //auto_tv_location.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list));

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        Log.d(TAG,"AutoComplete for location "+(String)object);
    }
}
