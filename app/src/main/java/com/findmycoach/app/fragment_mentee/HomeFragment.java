package com.findmycoach.app.fragment_mentee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.UserListActivity;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.Datum;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.util.AddressFromZip;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.GetLocationAsync;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, Callback {

    private AutoCompleteTextView locationInput;
    private Category category;
    private EditText nameInput;
    private Button searchButton;
    private ProgressDialog progressDialog;
    private TextView currentLocationText;
    private Button changeLocation;
    private RelativeLayout locationLayout;
    private LinearLayout timeBarrierLayout;
    public static String[] subCategoryIds;
    private View fragmentView;
    private String location = "";
    public static String location_auto_suggested_temp, location_auto_suggested;
    boolean flag_change_location = false;
    private boolean timeBarrier;
    private boolean isSearching = false;
    private static final String TAG = "FMC";
    private LinearLayout subCategoryLayout;
    private TextView subCategoryTextView;
    ArrayAdapter<String> arrayAdapter;
    private static int widthSubCategoryButton;

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

        initialize(fragmentView);
        updateLocationUI();
        applyActions();

        DataBase dataBase = DataBase.singleton(getActivity());

        Category categoryFromDb = dataBase.selectAllSubCategory();
        if (categoryFromDb.getData().size() < 1) {
            getCategories();
            Log.d(TAG, "sub category api called");
        } else {
            setTabForCategory(categoryFromDb);
            Log.d(TAG, "sub category api not called");
        }

        new GetLocationAsync(this, getActivity()).execute();
    }

    public void updateLocationFromAsync(String loc) {

        location = loc;
        location_auto_suggested_temp = location;
        location_auto_suggested = location_auto_suggested_temp;
        updateLocationUI();

    }


    private void setTabForCategory(Category categoryResponse) {

//        subCategoryLayout.removeAllViews();

        final List<Button> categoriesButtons = new ArrayList<Button>();

        this.category = categoryResponse;

        List<com.findmycoach.app.beans.category.Datum> data = category.getData();

        Collections.sort(data, new Comparator<com.findmycoach.app.beans.category.Datum>() {
            @Override
            public int compare(com.findmycoach.app.beans.category.Datum lhs, com.findmycoach.app.beans.category.Datum rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });

        subCategoryIds = new String[data.size()];


        if(widthSubCategoryButton == 0)
            widthSubCategoryButton = searchButton.getWidth() / (data.size() > 0 ? data.size() : 1);

        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthSubCategoryButton, 90);

        for (Datum datum : data) {

            StringBuilder subCategory = new StringBuilder();
            StringBuilder subCategoryId = new StringBuilder();
            int row = datum.getDataSub().size() + 1;
            subCategory.append("Select one#");
            subCategoryId.append("-1#");
            for (int x = 0; x < row - 1; x++) {
                subCategory.append(datum.getDataSub().get(x).getName() + "#");
                subCategoryId.append(datum.getDataSub().get(x).getId() + "#");
            }
            Log.d(TAG, datum.getDataSub().size() + "");
            Log.d(TAG, subCategory.toString());
            Log.d(TAG, subCategoryId.toString());

            Button button = new Button(getActivity());
            button.setTextColor(getActivity().getResources().getColor(R.color.white));
            button.setBackground(getActivity().getResources().getDrawable(R.drawable.button_unselected));
            button.setText(datum.getName());
            button.setLayoutParams(layoutParams);
            button.setTextSize(9.0f);
            subCategoryLayout.addView(button);
            categoriesButtons.add(button);
        }

        for (final Button btn : categoriesButtons) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Button button : categoriesButtons) {
                        button.setBackground(getActivity().getResources().getDrawable(R.drawable.button_unselected));
                        button.setTextColor(getActivity().getResources().getColor(R.color.white));
                    }
                    btn.setBackground(getActivity().getResources().getDrawable(R.drawable.button_selected));
                    btn.setTextColor(getActivity().getResources().getColor(R.color.purple));
                }
            });
        }

        fragmentView.findViewById(R.id.subCategoryLayoutParent).setVisibility(View.VISIBLE);
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




        final TextView preferredTimeTv = (TextView) fragmentView.findViewById(R.id.preferredTime);
        preferredTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeBarrierLayout.setVisibility(View.VISIBLE);
                timeBarrier = true;
                preferredTimeTv.setVisibility(View.GONE);
            }
        });

    }

    private void initialize(View view) {

        location_auto_suggested_temp = null;
        location_auto_suggested = null;

        locationInput = (AutoCompleteTextView) view.findViewById(R.id.input_location);
        nameInput = (EditText) view.findViewById(R.id.input_name);
        currentLocationText = (TextView) view.findViewById(R.id.current_location_text_view);
        searchButton = (Button) view.findViewById(R.id.action_search);
        changeLocation = (Button) view.findViewById(R.id.change_location);
        changeLocation.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity());
        currentLocationText.setText(location);
        locationInput.setText(location);
        locationLayout = (RelativeLayout) view.findViewById(R.id.current_location_layout);
        locationLayout = (RelativeLayout) view.findViewById(R.id.current_location_layout);
        timeBarrierLayout = (LinearLayout) view.findViewById(R.id.time_barrier_layout);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        timeBarrier = false;
        subCategoryLayout = (LinearLayout) fragmentView.findViewById(R.id.subCategoryLayout);
        subCategoryTextView = (TextView) fragmentView.findViewById(R.id.subCategoryTextView);

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                DashboardActivity.dashboardActivity.setupUIForShowHideKeyBoard(innerView);
            }
        }
    }

    private void updateLocationUI() {
        if (location.trim().equals("")) {
            flag_change_location = true;
            locationInput.setVisibility(View.VISIBLE);
            locationLayout.setVisibility(View.GONE);
        } else {
            currentLocationText.setText(location);
            locationInput.setText(location);
            flag_change_location = false;
            locationInput.setVisibility(View.GONE);
            locationLayout.setVisibility(View.VISIBLE);
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
//        String fromTiming = fromTimingInput.getText().toString();
//        String toTiming = toTimingInput.getText().toString();
        RequestParams requestParams = new RequestParams();
        requestParams.add("location", location);
        try {

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        requestParams.add("keyword", name);
        if (timeBarrier) {
//            requestParams.add("timing_from", fromTiming);
//            requestParams.add("timing_to", toTiming);
//
//            String week = (mon.isChecked() ? "1," : "0,") +
//                    (tue.isChecked() ? "1," : "0,") +
//                    (wed.isChecked() ? "1," : "0,") +
//                    (thr.isChecked() ? "1," : "0,") +
//                    (fri.isChecked() ? "1," : "0,") +
//                    (sat.isChecked() ? "1," : "0,") +
//                    (sun.isChecked() ? "1" : "0");
//            requestParams.add("weeks", week);
//            Log.d(TAG, "Selected weekdays : " + week);
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
            intent.putExtra("searched_keyword", -1);

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
