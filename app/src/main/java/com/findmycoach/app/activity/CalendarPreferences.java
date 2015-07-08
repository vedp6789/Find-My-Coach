package com.findmycoach.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.fragment.MyScheduleFragment;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ved on 14/6/15.
 */
public class CalendarPreferences extends Activity implements Callback {
    Spinner sp_class_type;
    AutoCompleteTextView auto_tv_location;
    Button b_save;
    public String TAG = "FMC";
    private String location = null;
    private String last_location_selected = null;
    ArrayAdapter<String> arrayAdapter;
    int class_type_index;
    int class_type_from_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mentor_calendar_preferences);
        initialize();

        ArrayAdapter arrayAdapter1_slot_types = new ArrayAdapter(this, R.layout.textview, getResources().getStringArray(R.array.class_type));
        arrayAdapter1_slot_types.setDropDownViewResource(R.layout.textview);
        sp_class_type.setAdapter(arrayAdapter1_slot_types);
        sp_class_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                class_type_index = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        class_type_from_pref = StorageHelper.getClassTypePreference(CalendarPreferences.this);
        if(class_type_from_pref != -1){
            sp_class_type.setSelection(class_type_from_pref);
        }


        listeners();

    }

    private void initialize() {

        sp_class_type = (Spinner) findViewById(R.id.sp_class_type);
        auto_tv_location = (AutoCompleteTextView) findViewById(R.id.auto_tv_location);
        b_save = (Button) findViewById(R.id.b_save);
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.calendar_preference));
        auto_tv_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                location = arrayAdapter.getItem(position).toString();
                last_location_selected = location;

            }
        });
    }

    private void listeners() {
        auto_tv_location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                location = null;    /* making location string null because if user do changes in location and doest not select location from suggested location then this location string should be null which is used to validate the location */
                if (location != null) {
                    Log.d(TAG, "Location after text changed  : " + location);

                } else {
                    Log.d(TAG, "Location after text changed : " + " not set correctly yet ");

                }
                String input = auto_tv_location.getText().toString();
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
                }
            }
        });

        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* adding selected value as preference for calendar view */
                StorageHelper.storeClassTypePreference(CalendarPreferences.this, class_type_index);
                if(MyScheduleFragment.myScheduleFragment != null){
                    MyScheduleFragment.myScheduleFragment.getCalendarDetailsAPICall();
                }
                finish();
            }
        });


    }

    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key", getResources().getString(R.string.google_location_api_key));
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.autoComplete(CalendarPreferences.this, requestParams, this, 32);
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
        arrayAdapter = new ArrayAdapter<String>(CalendarPreferences.this, android.R.layout.simple_list_item_1, list);
        auto_tv_location.setAdapter(arrayAdapter);
        //auto_tv_location.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list));

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        Log.d(TAG, "AutoComplete for location " + (String) object);

    }
}
