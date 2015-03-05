package com.findmycoach.app.fragment_mentee;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.findmycoach.app.beans.subcategory.Datum;
import com.findmycoach.app.beans.subcategory.SubCategory;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, Callback {

    private AutoCompleteTextView locationInput;
//    private AutoCompleteTextView categoryInput;
//    private AutoCompleteTextView subCategoryInput;
    private TabHost tabHost;
    LocalActivityManager localActivityManager;
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

    private static final String TAG="FMC";

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_mentee, container, false);
        initialize(view);
        applyActions();
        localActivityManager.dispatchCreate(savedInstanceState);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTabForCategory();
    }

    private void setTabForCategory() {
        tabHost.setup(localActivityManager);


        TabHost.TabSpec education = tabHost.newTabSpec("Education");
        education.setIndicator("Education");
        TabHost.TabSpec sport = tabHost.newTabSpec("Sport");
        sport.setIndicator("Sport");
        TabHost.TabSpec vocation = tabHost.newTabSpec("Vocation");
        vocation.setIndicator("Vocation");

        Intent educationIntent = new Intent(getActivity(), SubCategoryActivity.class);
        educationIntent.putExtra("row", 9);
        educationIntent.putExtra("category","Education");

        Intent sportIntent = new Intent(getActivity(), SubCategoryActivity.class);
        sportIntent.putExtra("row", 15);
        sportIntent.putExtra("category","Sports");

        Intent vocationIntent = new Intent(getActivity(), SubCategoryActivity.class);
        vocationIntent.putExtra("row", 3);
        vocationIntent.putExtra("category","Vocation");

        education.setContent(educationIntent);
        sport.setContent(sportIntent);
        vocation.setContent(vocationIntent);
        tabHost.addTab(education);
        tabHost.addTab(sport);
        tabHost.addTab(vocation);
    }

    @Override
    public void onStart() {
        super.onStart();
        getCategories();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locationInput != null) {
            locationInput.setText(NetworkManager.getCurrentLocation(getActivity()));
            if (locationLayout != null) {
                locationLayout.setVisibility(View.VISIBLE);
                locationInput.setVisibility(View.GONE);
            }
        }
        if (currentLocationText != null) {
            currentLocationText.setText(NetworkManager.getCurrentLocation(getActivity()));
        }
        localActivityManager.dispatchResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        localActivityManager.dispatchPause(true);
    }

    private void getCategories() {
        RequestParams requestParams = new RequestParams();
        String authToken = StorageHelper.getUserDetails(getActivity(), "auth_token");
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.getCategories(getActivity(), requestParams, authToken, this, 34);
    }

    private void applyActions() {
        locationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = locationInput.getText().toString();
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
                }
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
//        categoryInput = (AutoCompleteTextView) view.findViewById(R.id.input_category);
//        subCategoryInput = (AutoCompleteTextView) view.findViewById(R.id.input_sub_category);
        nameInput = (EditText) view.findViewById(R.id.input_name);
        fromTimingInput = (AutoCompleteTextView) view.findViewById(R.id.from_timing);
        toTimingInput = (AutoCompleteTextView) view.findViewById(R.id.to_timing);
        currentLocationText = (TextView) view.findViewById(R.id.current_location_text_view);
        searchButton = (Button) view.findViewById(R.id.action_search);
        changeLocation = (Button) view.findViewById(R.id.change_location);
        changeLocation.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        currentLocationText.setText(NetworkManager.getCurrentLocation(getActivity()));
        locationInput.setText(NetworkManager.getCurrentLocation(getActivity()));
        progressDialog = new ProgressDialog(getActivity());
        locationLayout = (LinearLayout) view.findViewById(R.id.current_location_layout);
        timeBarrierLayout = (LinearLayout) view.findViewById(R.id.time_barrier_layout);
        timeBarrier = (RadioButton) view.findViewById(R.id.on_time);
        noTimeBarrier = (RadioButton) view.findViewById(R.id.no_time);
        tabHost = (TabHost) view.findViewById(R.id.tabhost);
        localActivityManager = new LocalActivityManager(getActivity(), false);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
    }

    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key", getResources().getString(R.string.google_location_api_key));
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.autoComplete(getActivity(), requestParams, this, 32);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_search) {
            callSearchApi();
        }
        if (v.getId() == R.id.change_location) {
            locationInput.setVisibility(View.VISIBLE);
            locationLayout.setVisibility(View.GONE);
        }
    }

    private void callSearchApi() {
        progressDialog.show();
        String location = locationInput.getText().toString();
//        String category = categoryInput.getText().toString();
//        String subCategory = subCategoryInput.getText().toString();
        String name = nameInput.getText().toString();
        String fromTiming = fromTimingInput.getText().toString();
        String toTiming = toTimingInput.getText().toString();
        RequestParams requestParams = new RequestParams();
        requestParams.add("location", location);
//        requestParams.add("category_id", category);
//        requestParams.add("subcategory_id", subCategory);
        requestParams.add("keyword", name);
        requestParams.add("timing_from", fromTiming);
        requestParams.add("timing_to", toTiming);
        requestParams.add("id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        String authToken = StorageHelper.getUserDetails(getActivity(), "auth_token");
        NetworkClient.search(getActivity(), requestParams, authToken, this, 6);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        if (object instanceof Suggestion) {
            Suggestion suggestion = (Suggestion) object;
            updateAutoSuggestion(suggestion);
//        } else if (object instanceof Category) {
//            applyCategoryAdapter((Category) object);
//        } else if (object instanceof SubCategory) {
//            applySubCategoryAdapter((SubCategory) object);
        } else {
            progressDialog.dismiss();
            Intent intent = new Intent(getActivity(), UserListActivity.class);
            intent.putExtra("list", (String) object);
            startActivity(intent);
        }
    }

    private void applySubCategoryAdapter(SubCategory subCategory) {
        final List<Datum> data = subCategory.getData();
        List<String> categories = new ArrayList<String>();
        for (int index = 0; index < data.size(); index++) {
            com.findmycoach.app.beans.subcategory.Datum datum = data.get(index);
            categories.add(datum.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, categories);
//        subCategoryInput.setAdapter(adapter);
    }

    private void applyCategoryAdapter(Category category) {
        final List<com.findmycoach.app.beans.category.Datum> data = category.getData();
        final List<String> categories = new ArrayList<String>();
        for (int index = 0; index < data.size(); index++) {
            com.findmycoach.app.beans.category.Datum datum = data.get(index);
            categories.add(datum.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, categories);
//        categoryInput.setAdapter(adapter);
//        categoryInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String selectedCategory = categoryInput.getText().toString();
//                Log.d(TAG, "Selected Category:" + selectedCategory);
//                Log.d(TAG, "All Categories:" + categories);
//                String selectedCategoryId = data.get(categories.indexOf(selectedCategory)).getId();
//                Log.d(TAG, "Selected Id:" + selectedCategoryId);
//                getSubCategories(selectedCategoryId);
//            }
//        });
    }

    private void getSubCategories(String selectedCategoryId) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", selectedCategoryId);
        String authToken = StorageHelper.getUserDetails(getActivity(), "auth_token");
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.getSubCategories(getActivity(), requestParams, authToken, this, 33);
    }

    private void updateAutoSuggestion(Suggestion suggestion) {
        ArrayList<String> list = new ArrayList<String>();
        List<Prediction> suggestions = suggestion.getPredictions();
        for (int index = 0; index < suggestions.size(); index++) {
            list.add(suggestions.get(index).getDescription());
        }
        locationInput.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list));
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
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
