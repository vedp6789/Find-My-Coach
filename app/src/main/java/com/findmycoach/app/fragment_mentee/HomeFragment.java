package com.findmycoach.app.fragment_mentee;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.UserListActivity;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.Datum;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.fragment.TimePickerFragment;
import com.findmycoach.app.util.AddressFromZip;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.GetLocationAsync;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, Callback, TimePickerDialog.OnTimeSetListener {

    private AutoCompleteTextView locationInput;
    private Category category;
    private Button searchButton;
    private ProgressDialog progressDialog;
    private TextView currentLocationText;
    private TextView fromTimingInput;
    private TextView toTimingInput;
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
    private List<Button> daysButton;
    private Button selectedCategory;
    private String type;
    private TextView textView;

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

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if (widthSubCategoryButton == 0)
            widthSubCategoryButton = (size.x - (int) getResources().getDimension(R.dimen.category_button_padding)) / (data.size() > 0 ? data.size() : 1);


        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthSubCategoryButton, (int) getResources().getDimension(R.dimen.login_button_height));

        final HashMap<String, String[]> subCatNameMap = new HashMap<>();
        final HashMap<String, String[]> subCatIdMap = new HashMap<>();

        for (Datum datum : data) {

            int row = datum.getDataSub().size() + 1;
            String[] name = new String[row - 1];
            String[] id = new String[row - 1];
            for (int x = 0; x < row - 1; x++) {
                name[x] = datum.getDataSub().get(x).getName();
                id[x] = datum.getDataSub().get(x).getId();
            }
            Log.d(TAG, datum.getDataSub().size() + "");

            subCatNameMap.put(datum.getId(), name);
            subCatIdMap.put(datum.getId(), id);

            Button button = new Button(getActivity());
            button.setTextColor(getActivity().getResources().getColor(R.color.white));
            button.setBackground(getActivity().getResources().getDrawable(R.drawable.button_unselected));
            button.setText(datum.getName());
            button.setLayoutParams(layoutParams);
            button.setTag(datum.getId());
            button.setTextSize(12.0f);
            subCategoryLayout.addView(button);
            categoriesButtons.add(button);
        }

        selectedCategory = null;

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


                    String first = subCatNameMap.get(btn.getTag())[0];
                    String next = "<font color='#AFA4C4'> - beginner</font>";
                    subCategoryTextView.setText(Html.fromHtml(first + next));
                    subCategoryTextView.setTag(subCatIdMap.get(btn.getTag())[0]);

                    selectedCategory = btn;
                }
            });
        }

        subCategoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategory != null) {

                    type = getActivity().getResources().getString(R.string.beginner).toLowerCase();

                    String selectedType = subCategoryTextView.getText().toString().toLowerCase();

                    if (selectedType.contains(getActivity().getResources().getString(R.string.intermediate).toLowerCase()))
                        type = getActivity().getResources().getString(R.string.intermediate).toLowerCase();

                    if (selectedType.contains(getActivity().getResources().getString(R.string.advance).toLowerCase()))
                        type = getActivity().getResources().getString(R.string.advance).toLowerCase();

                    final int purple = getActivity().getResources().getColor(R.color.purple);
                    final int purpleLight = getActivity().getResources().getColor(R.color.purple_light);

                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.sub_category_dialog);

                    final Button beginner = (Button) dialog.findViewById(R.id.beginner);
                    final Button intermediate = (Button) dialog.findViewById(R.id.intermediate);
                    final Button advance = (Button) dialog.findViewById(R.id.advance);

                    if (type.equalsIgnoreCase(getActivity().getResources().getString(R.string.beginner)))
                        beginner.setBackgroundColor(purpleLight);
                    else if (type.equalsIgnoreCase(getActivity().getResources().getString(R.string.intermediate)))
                        intermediate.setBackgroundColor(purpleLight);
                    else if (type.equalsIgnoreCase(getActivity().getResources().getString(R.string.advance)))
                        advance.setBackgroundColor(purpleLight);

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();

                            if (id == R.id.beginner) {
                                beginner.setBackgroundColor(purpleLight);
                                type = getActivity().getResources().getString(R.string.beginner).toLowerCase();
                            } else
                                beginner.setBackgroundColor(purple);

                            if (id == R.id.intermediate) {
                                intermediate.setBackgroundColor(purpleLight);
                                type = getActivity().getResources().getString(R.string.intermediate).toLowerCase();
                            } else
                                intermediate.setBackgroundColor(purple);

                            if (id == R.id.advance) {
                                advance.setBackgroundColor(purpleLight);
                                type = getActivity().getResources().getString(R.string.advance).toLowerCase();
                            } else
                                advance.setBackgroundColor(purple);


                        }
                    };

                    beginner.setOnClickListener(listener);
                    intermediate.setOnClickListener(listener);
                    advance.setOnClickListener(listener);


                    ListView listView = (ListView) dialog.findViewById(R.id.subCategoryListView);

                    listView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.textview, subCatNameMap.get(selectedCategory.getTag())));

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String first = subCatNameMap.get(selectedCategory.getTag())[position];
                            String next = "<font color='#AFA4C4'> - " + type + "</font>";
                            subCategoryTextView.setText(Html.fromHtml(first + next));
                            subCategoryTextView.setTag(subCatIdMap.get(selectedCategory.getTag())[position]);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }
            }


        });

        if (categoriesButtons.size() > 0) {
            categoriesButtons.get(0).performClick();
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

        final Calendar c = Calendar.getInstance();

        final TextView preferredTimeTv = (TextView) fragmentView.findViewById(R.id.preferredTime);
        preferredTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeBarrierLayout.setVisibility(View.VISIBLE);
                timeBarrier = true;
                preferredTimeTv.setVisibility(View.GONE);

                int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                int hour = hourOfDay % 12;
                if (hourOfDay == 12)
                    hour = 12;
                String min = "";
                if (minute > 52 || minute < 8)
                    min = "00";
                else if (minute > 7 && minute < 23)
                    min = "15";
                else if (minute > 22 && minute < 38)
                    min = "30";
                else if (minute > 37 && minute < 53)
                    min = "45";
                fromTimingInput.setText(" " + (hour < 10 ? ("0" + hour) : hour) + ":" + min + (hourOfDay > 11 ? " PM" : " AM"));
                fromTimingInput.setTag(fromTimingInput.getId(), hourOfDay++ + ":" + min);
                if (hour == 12)
                    hour = 1;
                else
                    hour++;
                toTimingInput.setText(" " + (hour < 10 ? ("0" + hour) : hour) + ":" + min + (hourOfDay > 11 ? " PM" : " AM"));
                toTimingInput.setTag(toTimingInput.getId(), hourOfDay + ":" + min);

                getSelectedButtons();
            }
        });

        final Button clearFilter = (Button) fragmentView.findViewById(R.id.clear);
        clearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daysButton = null;
                timeBarrierLayout.setVisibility(View.GONE);
                timeBarrier = false;
                preferredTimeTv.setVisibility(View.VISIBLE);
            }
        });


        fragmentView.findViewById(R.id.fromTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePicker = new TimePickerFragment(getActivity(), HomeFragment.this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
                textView = fromTimingInput;
                timePicker.show();
            }
        });

        fragmentView.findViewById(R.id.toTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = c.get(Calendar.HOUR_OF_DAY);
                TimePickerFragment timePicker = new TimePickerFragment(getActivity(), HomeFragment.this, ++hour, c.get(Calendar.MINUTE), false);
                textView = toTimingInput;
                timePicker.show();
            }
        });

    }

    private void getSelectedButtons() {
        daysButton = new ArrayList<>();

        daysButton.add((Button) fragmentView.findViewById(R.id.sun));
        daysButton.add((Button) fragmentView.findViewById(R.id.mon));
        daysButton.add((Button) fragmentView.findViewById(R.id.tue));
        daysButton.add((Button) fragmentView.findViewById(R.id.wed));
        daysButton.add((Button) fragmentView.findViewById(R.id.thu));
        daysButton.add((Button) fragmentView.findViewById(R.id.fri));
        daysButton.add((Button) fragmentView.findViewById(R.id.sat));

        for (final Button b : daysButton) {
            b.setTag(b.getId(), 0);

            b.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            b.setTextColor(getActivity().getResources().getColor(R.color.purple));

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (Integer) b.getTag(b.getId());
                    if (tag == 0) {
                        b.setTag(b.getId(), 1);
                        b.setBackgroundColor(getActivity().getResources().getColor(R.color.purple_light));
                        b.setTextColor(getActivity().getResources().getColor(R.color.white));
                    } else {
                        b.setTag(b.getId(), 0);
                        b.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                        b.setTextColor(getActivity().getResources().getColor(R.color.purple));
                    }

                }
            });
        }

    }

    private void initialize(View view) {

        location_auto_suggested_temp = null;
        location_auto_suggested = null;

        locationInput = (AutoCompleteTextView) view.findViewById(R.id.input_location);
        currentLocationText = (TextView) view.findViewById(R.id.current_location_text_view);
        searchButton = (Button) view.findViewById(R.id.action_search);
        changeLocation = (Button) view.findViewById(R.id.change_location);
        changeLocation.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        currentLocationText.setText(location);
        locationInput.setText(location);
        locationLayout = (RelativeLayout) view.findViewById(R.id.current_location_layout);
        locationLayout = (RelativeLayout) view.findViewById(R.id.current_location_layout);
        timeBarrierLayout = (LinearLayout) view.findViewById(R.id.time_barrier_layout);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        timeBarrier = false;
        subCategoryLayout = (LinearLayout) fragmentView.findViewById(R.id.subCategoryLayout);
        subCategoryTextView = (TextView) fragmentView.findViewById(R.id.subCategoryTextView);

        toTimingInput = (TextView) fragmentView.findViewById(R.id.to_timing);
        fromTimingInput = (TextView) fragmentView.findViewById(R.id.from_timing);

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
        RequestParams requestParams = new RequestParams();
        requestParams.add("location", location);
        requestParams.add("subcategory_id", subCategoryTextView.getTag() + "");
        Log.e(TAG, "subcategory_id : " + subCategoryTextView.getTag() + " == " + location);

        if (timeBarrier) {
            String fromTiming = (String) fromTimingInput.getTag(fromTimingInput.getId());
            String toTiming = (String) toTimingInput.getTag(toTimingInput.getId());
            try {
                JSONObject timings = new JSONObject();
                timings.put("from", fromTiming);
                timings.put("to", toTiming);
                requestParams.add("timings", timings.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String week = "";

            String[] daysArray = new String[]{"Su", "M", "T", "W", "Th", "F", "S"};

            for (int x = 0; x < 7; x++) {
                Button b = daysButton.get(x);
                String val = b.getTag(b.getId()) + "";
                if (val.trim().equals("1")) {
                    week = week + "," + daysArray[x];
                }
            }
            try {
                week = week.replaceFirst(",", "");
            } catch (Exception ignored) {
            }
            requestParams.add("weeks", week);
            Log.d(TAG, "Selected weekdays : " + week + ", from time : " + fromTiming + ", to time : " + toTiming);
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
            intent.putExtra("search_for", subCategoryTextView.getText().toString());
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
        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.textview, list);
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

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int hour = hourOfDay % 12;
        if (hourOfDay == 12)
            hour = 12;
        if (textView != null) {
            String min = "";
            if (minute == 0)
                min = "00";
            else if (minute == 1)
                min = "15";
            else if (minute == 2)
                min = "30";
            else if (minute == 3)
                min = "45";
            textView.setText(" " + (hour < 10 ? ("0" + hour) : hour) + ":" + min + (hourOfDay > 11 ? " PM" : " AM"));
            textView.setTag(textView.getId(), hourOfDay + ":" + min);
            textView = null;
        }
    }
}
