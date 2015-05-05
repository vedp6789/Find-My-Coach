package com.findmycoach.app.fragment_mentee;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.SubCategoryActivity;
import com.findmycoach.app.activity.UserListActivity;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.util.AddressFromZip;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, Callback {

    private AutoCompleteTextView locationInput;
    private TabHost tabHost;
    private LocalActivityManager localActivityManager;
    private Category category;
    private EditText nameInput;
    private AutoCompleteTextView fromTimingInput;
    private AutoCompleteTextView toTimingInput;
    private Button searchButton;
    private ProgressDialog progressDialog;
    private TextView currentLocationText;
    private Button changeLocation;
    private LinearLayout locationLayout;
    private LinearLayout timeBarrierLayout;
    private RadioButton timeBarrier;
    private RadioButton noTimeBarrier;
    private RadioGroup radioGroup;
    private int FLAG;
    public static String[] subCategoryIds;
    private int tabIndex;
    private View fragmentView;
    private String location;
    public static String location_auto_suggested_temp, location_auto_suggested;
    boolean flag_change_location = false;

    private CheckBox mon, tue, wed, thr, fri, sat, sun;
    private boolean isSearching = false;
    private static final String TAG = "FMC";
    ArrayAdapter<String> arrayAdapter;

    public HomeFragment() {
        // Required empty public constructor
        subCategoryIds = null;
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location_auto_suggested_temp = null;
        location_auto_suggested = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_home_mentee, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        location_auto_suggested_temp = null;
        location_auto_suggested = null;

        location = NetworkManager.getCurrentLocation(getActivity());
        location_auto_suggested_temp = location;
        location_auto_suggested = location_auto_suggested_temp;

        initialize(fragmentView);
        if (location.equals("")) {
            flag_change_location = true;
            locationInput.setVisibility(View.VISIBLE);
            locationLayout.setVisibility(View.GONE);
        }
        applyActions();
        localActivityManager.dispatchCreate(savedInstanceState);

        DataBase dataBase = DataBase.singleton(getActivity());
        ;
        Category categoryFromDb = dataBase.selectAllSubCategory();
        if (categoryFromDb.getData().size() < 1) {
            getCategories();
            Log.d(TAG, "sub category api called");
        } else {
            setTabForCategory(categoryFromDb);
            Log.d(TAG, "sub category api not called");
        }
    }


    private void setTabForCategory(Category categoryResponse) {
        this.category = categoryResponse;
        if (categoryResponse.getData().size() < 1 || FLAG > 0)
            return;

        tabHost.setup(localActivityManager);
        subCategoryIds = new String[category.getData().size()];
        for (int i = 0; i < category.getData().size(); i++) {
            com.findmycoach.app.beans.category.Datum datum = category.getData().get(i);

            TabHost.TabSpec singleCategory = tabHost.newTabSpec(i + "");
            singleCategory.setIndicator(datum.getName());

            Intent intent = new Intent(getActivity(), SubCategoryActivity.class);
            StringBuilder subCategory = new StringBuilder();
            StringBuilder subCategoryId = new StringBuilder();
            int row = datum.getDataSub().size() + 1;
            subCategory.append("Select one#");
            subCategoryId.append("-1#");
            for (int x = 0; x < row - 1; x++) {
                subCategory.append(datum.getDataSub().get(x).getName() + "#");
                subCategoryId.append(datum.getDataSub().get(x).getId() + "#");
            }
//            Log.d(TAG, datum.getDataSub().size()+"");
//            Log.d(TAG, subCategory.toString());
//            Log.d(TAG, subCategoryId.toString());
            intent.putExtra("row", row);
            intent.putExtra("sub_category", subCategory.toString());
            intent.putExtra("sub_category_id", subCategoryId.toString());
            intent.putExtra("column_index", i);
            singleCategory.setContent(intent);
            tabHost.addTab(singleCategory);
        }

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                tabIndex = Integer.parseInt(tabId);
            }
        });

        FLAG = 1;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void getCategories() {
        RequestParams requestParams = new RequestParams();
        String authToken = StorageHelper.getUserDetails(getActivity(), "auth_token");
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.getCategories(getActivity(), requestParams, authToken, this, 34);
    }

    private void applyActions() {
        locationInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(locationInput.getWindowToken(), 0);
                }
            }
        });

        locationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                location_auto_suggested_temp = null;
                String input = locationInput.getText().toString();
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
                    if (input.length() > 4 && input.length() < 8) {
                        try {
                            int zipCode = Integer.parseInt(input);
                            new AddressFromZip(getActivity(), locationInput).execute(zipCode + "");
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
        locationInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                location_auto_suggested_temp = arrayAdapter.getItem(position);
                location_auto_suggested = location_auto_suggested_temp;
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(locationInput.getWindowToken(), 0);
            }
        });
        String[] period = getResources().getStringArray(R.array.time1);
        fromTimingInput.setThreshold(1);
        fromTimingInput.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, period) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setTextColor(Color.BLACK);
                return view;
            }
        });
        toTimingInput.setThreshold(1);
        toTimingInput.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, period) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setTextColor(Color.BLACK);
                return view;
            }
        });
        timeBarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeBarrier.isChecked()) {
                    timeBarrier.setChecked(true);
                    noTimeBarrier.setChecked(false);
                    timeBarrierLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        noTimeBarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noTimeBarrier.isChecked()) {
                    noTimeBarrier.setChecked(true);
                    timeBarrier.setChecked(false);
                    timeBarrierLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initialize(View view) {
        locationInput = (AutoCompleteTextView) view.findViewById(R.id.input_location);
        nameInput = (EditText) view.findViewById(R.id.input_name);
        fromTimingInput = (AutoCompleteTextView) view.findViewById(R.id.from_timing);
        toTimingInput = (AutoCompleteTextView) view.findViewById(R.id.to_timing);
        currentLocationText = (TextView) view.findViewById(R.id.current_location_text_view);
        searchButton = (Button) view.findViewById(R.id.action_search);
        changeLocation = (Button) view.findViewById(R.id.change_location);
        changeLocation.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        progressDialog = new ProgressDialog(getActivity());
        currentLocationText.setText(location);
        locationInput.setText(location);
        locationLayout = (LinearLayout) view.findViewById(R.id.current_location_layout);
        timeBarrierLayout = (LinearLayout) view.findViewById(R.id.time_barrier_layout);
        timeBarrier = (RadioButton) view.findViewById(R.id.on_time);
        noTimeBarrier = (RadioButton) view.findViewById(R.id.no_time);
        tabHost = (TabHost) view.findViewById(R.id.tabhost);
        localActivityManager = new LocalActivityManager(getActivity(), false);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        mon = (CheckBox) view.findViewById(R.id.mon);
        tue = (CheckBox) view.findViewById(R.id.tue);
        wed = (CheckBox) view.findViewById(R.id.wed);
        thr = (CheckBox) view.findViewById(R.id.thu);
        fri = (CheckBox) view.findViewById(R.id.fri);
        sat = (CheckBox) view.findViewById(R.id.sat);
        sun = (CheckBox) view.findViewById(R.id.sun);

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                DashboardActivity.dashboardActivity.setupUIForShowHideKeyBoard(innerView);
            }
        }
    }

    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key", getResources().getString(R.string.google_location_api_key));
//        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.autoComplete(getActivity(), requestParams, this, 32);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_search) {
            callSearchApi();
        }
        if (v.getId() == R.id.change_location) {
            flag_change_location = true;
            locationInput.setVisibility(View.VISIBLE);
            locationLayout.setVisibility(View.GONE);
        }
    }

    private void callSearchApi() {
        if (!NetworkManager.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.check_network_connection), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isSearching) {
            Toast.makeText(getActivity(), getResources().getString(R.string.search_is_already_called), Toast.LENGTH_SHORT).show();
            return;
        }
        if (flag_change_location) {
            if (validateLocation()) {
                goForSearch();
            }
        } else {
            goForSearch();
        }

    }

    void goForSearch() {
        isSearching = true;
        progressDialog.show();
        String location = locationInput.getText().toString();
        String name = nameInput.getText().toString();
        String fromTiming = fromTimingInput.getText().toString();
        String toTiming = toTimingInput.getText().toString();
        RequestParams requestParams = new RequestParams();
        requestParams.add("location", location);
        try {
            Log.d(TAG, "Sub category id : " + subCategoryIds[tabIndex]);
            if (!subCategoryIds[tabIndex].trim().equals("-1"))
                requestParams.add("subcategory_id", subCategoryIds[tabIndex]);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        requestParams.add("keyword", name);
        if (timeBarrier.isChecked()) {
            requestParams.add("timing_from", fromTiming);
            requestParams.add("timing_to", toTiming);

            String week = (mon.isChecked() ? "1," : "0,") +
                    (tue.isChecked() ? "1," : "0,") +
                    (wed.isChecked() ? "1," : "0,") +
                    (thr.isChecked() ? "1," : "0,") +
                    (fri.isChecked() ? "1," : "0,") +
                    (sat.isChecked() ? "1," : "0,") +
                    (sun.isChecked() ? "1" : "0");
            requestParams.add("weeks", week);
            Log.d(TAG, "Selected weekdays : " + week);
        }
        requestParams.add("id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        String authToken = StorageHelper.getUserDetails(getActivity(), "auth_token");
        NetworkClient.search(getActivity(), requestParams, authToken, this, 6);
    }

    boolean validateLocation() {
        if (locationInput.getText().toString() != null) {
            if (locationInput.getText().toString().trim().equalsIgnoreCase("")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.choose_suggested_location), Toast.LENGTH_LONG).show();
                locationInput.setError(getResources().getString(R.string.choose_suggested_location));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        locationInput.setError(null);
                    }
                }, 3500);
                return false;
            }
            if (!locationInput.getText().toString().trim().equalsIgnoreCase(location_auto_suggested_temp)) {
                if (!locationInput.getText().toString().equalsIgnoreCase(location_auto_suggested)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.choose_suggested_location), Toast.LENGTH_LONG).show();
                    locationInput.setError(getResources().getString(R.string.choose_suggested_location));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            locationInput.setError(null);
                        }
                    }, 3500);
                    return false;
                }

            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.choose_suggested_location), Toast.LENGTH_LONG).show();
            locationInput.setError(getResources().getString(R.string.choose_suggested_location));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    locationInput.setError(null);
                }
            }, 3500);
            return false;
        }
        return true;

    }


    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        if (object instanceof Suggestion) {
            Suggestion suggestion = (Suggestion) object;
            updateAutoSuggestion(suggestion);
        } else if (object instanceof Category) {
            setTabForCategory((Category) object);
            DataBase dataBase = DataBase.singleton(getActivity());
            dataBase.insertData((Category) object);
        } else {
            progressDialog.dismiss();
            Intent intent = new Intent(getActivity(), UserListActivity.class);
            intent.putExtra("list", (String) object);
            intent.putExtra("searched_keyword", subCategoryIds[tabIndex]);
            Log.e(TAG, subCategoryIds[tabIndex]);
            startActivity(intent);
            isSearching = false;
        }
    }

    private void updateAutoSuggestion(Suggestion suggestion) {
        ArrayList<String> list = new ArrayList<String>();
        List<Prediction> suggestions = suggestion.getPredictions();
        for (int index = 0; index < suggestions.size(); index++) {
            list.add(suggestions.get(index).getDescription());
        }
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
        locationInput.setAdapter(arrayAdapter);
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        isSearching = false;
        if (calledApiValue != 34)
            Toast.makeText(getActivity(), (String) object, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
