package com.findmycoach.app.fragment_mentee;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.UserListActivity;
import com.findmycoach.app.adapter.InterestsAdapter;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.Datum;
import com.findmycoach.app.beans.category.DatumSub;
import com.findmycoach.app.beans.student.Address;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.fragment.TimePickerFragment;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.PlaceAutocompleteAdapter;
import com.findmycoach.app.util.StorageHelper;
import com.findmycoach.app.views.ChizzleButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, Callback, TimePickerDialog.OnTimeSetListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,LocationListener {

    private AutoCompleteTextView locationInput;
    private Button searchButton;
    private ImageButton ibDeleteAddressImageButton;
    private ProgressDialog progressDialog;
    public TextView currentLocationText;
    private TextView fromTimingInput;
    private RelativeLayout editLocation, selectLocation, rlAutoSuggestionAddress;
    private LinearLayout timeBarrierLayout;
    public static String[] subCategoryIds;
    private View fragmentView;
    private String location = "";
    private String userCurrentAddressFromGPS = "";
    boolean isEditLocationEnabled;
    private boolean timeBarrier;
    private boolean isSearching = false;
    private static final String TAG = "FMC";
    private LinearLayout subCategoryLayout;
    private TextView subCategoryTextView;
    private ArrayAdapter<String> arrayAdapter;
    private List<com.findmycoach.app.views.ChizzleButton> daysButton;
    public static HomeFragment homeFragmentMentee;
    private String type;
    private ProfileResponse profileResponse;
    public int mentorFor, coachingType, numberOfTabsToBeShown;


    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private LocationRequest mLocationRequest;


    public HomeFragment() {
        subCategoryIds = null;
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
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

        String categoryData = dataBase.getAll();
        Category categoryFromDb = new Gson().fromJson(categoryData, Category.class);
        if (categoryFromDb.getData() == null || categoryFromDb.getData().size() < 1) {
            getCategories();
            Log.d(TAG, "sub category api called");
        } else {
            setTabForCategory(categoryFromDb);
            Log.d(TAG, "sub category api not called");
        }

        try {
            Log.e(TAG, categoryFromDb.getOtpLength() + " : " + categoryFromDb.getOtpPosition());
        } catch (Exception ignored) {
        }

        homeFragmentMentee = this;
    }


    private void setTabForCategory(Category categoryResponse) {
        List<Datum> data = categoryResponse.getData();
        if (data.size() < 1)
            return;

        Collections.sort(data, new Comparator<Datum>() {
            @Override
            public int compare(com.findmycoach.app.beans.category.Datum lhs,
                               com.findmycoach.app.beans.category.Datum rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int widthSubCategoryButton = (size.x - (int) getResources()
                .getDimension(R.dimen.category_button_padding)) / (data.size() > 0
                ? data.size() : 1);

        final ViewGroup.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(widthSubCategoryButton, (int) getResources()
                .getDimension(R.dimen.login_button_height));
        final List<com.findmycoach.app.views.ChizzleButton> buttonList = new ArrayList<>();

        for (Datum datum : data) {
            final ChizzleButton button = new ChizzleButton(getActivity());
            button.setTextColor(getActivity().getResources().getColor(R.color.white));
            button.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.button_unselected));
            button.setText(datum.getName());
            button.setLayoutParams(layoutParams);

            if (datum.getSubCategories().size() > 0) {
                button.setId(fragmentView.getId());
                button.setTag(fragmentView.getId(), datum.getSubCategories());
            } else if (datum.getCategories().size() > 0) {
                button.setId(subCategoryLayout.getId());
                button.setTag(subCategoryLayout.getId(), datum.getCategories());
            }

            button.setTextSize(14.0f);
            subCategoryLayout.addView(button);
            buttonList.add((ChizzleButton) button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Button btn : buttonList) {
                        btn.setBackgroundDrawable(getActivity().getResources()
                                .getDrawable(R.drawable.button_unselected));
                        btn.setTextColor(getActivity().getResources().getColor(R.color.white));
                    }
                    button.setBackgroundDrawable(getActivity().getResources()
                            .getDrawable(R.drawable.button_selected));
                    button.setTextColor(getActivity().getResources().getColor(R.color.purple));

                    if (v.getId() == subCategoryLayout.getId()) {
                        final List<Datum> d = (List<Datum>) v.getTag(v.getId());

                        String first = d.get(0).getSubCategories().get(0).getName();
                        String next = "<font color='#AFA4C4'> - " + type + "</font> (" + d.get(0).getName() + ")";
                        subCategoryTextView.setText(Html.fromHtml(first + next));
                        if (d.get(0).getSubCategories() != null) {
                            subCategoryTextView.setTag(d.get(0).getSubCategories().get(0).getId());

                        } else {
                            subCategoryTextView.setTag(d.get(0).getId());
                        }
                        subCategoryTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                ListView listView = new ListView(getActivity());
                                dialog.setContentView(listView);
                                List<InterestsAdapter.SubCategoryItems> itemses
                                        = new ArrayList<InterestsAdapter.SubCategoryItems>();
                                for (Datum datum1 : d) {
                                    itemses.add(new InterestsAdapter
                                            .SubCategoryItems(datum1.getName(), 1));

                                    InterestsAdapter adapter
                                            = new InterestsAdapter(getActivity(), itemses);
                                    listView.setAdapter(adapter);

                                    listView.setOnItemClickListener(new AdapterView
                                            .OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent,
                                                                View view, int position, long id) {
                                            showSubCategoryDialog(getResources()
                                                            .getColor(R.color.category_selected),
                                                    getResources().getColor(R.color.category_unselected),
                                                    d.get(position).getSubCategories(),
                                                    d.get(position).getName());
                                            dialog.dismiss();
                                        }
                                    });
                                }
                                dialog.show();

                            }
                        });

                    } else if (v.getId() == fragmentView.getId()) {
                        final List<DatumSub> d = (List<DatumSub>) v.getTag(v.getId());

                        String next = "<font color='#AFA4C4'> - " + type + "</font>";
                        subCategoryTextView.setText(Html.fromHtml(d.get(0).getName() + next));
                        subCategoryTextView.setTag(d.get(0).getId());
                        subCategoryTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showSubCategoryDialog(getResources().getColor(R.color.category_selected)
                                        , getResources().getColor(R.color.category_unselected), d, null);
                            }
                        });
                    }
                }
            });
        }

        buttonList.get(0).callOnClick();
        fragmentView.findViewById(R.id.subCategoryLayoutParent).setVisibility(View.VISIBLE);
    }

    private void showSubCategoryDialog(final int purpleLight, final int purple,
                                       final List<DatumSub> d, final String categoryName) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sub_category_dialog);

        final Button beginner = (Button) dialog.findViewById(R.id.beginner);
        final Button intermediate = (Button) dialog.findViewById(R.id.intermediate);
        final Button advance = (Button) dialog.findViewById(R.id.advance);
        final ListView listView = (ListView) dialog.findViewById(R.id.subCategoryListView);
        final List<String> options = new ArrayList<>();
        for (DatumSub datumSub : d)
            options.add(datumSub.getName());

        if (type.equalsIgnoreCase(getActivity().getResources().getString(R.string.beginner)))
            beginner.setBackgroundColor(purpleLight);
        else if (type.equalsIgnoreCase(getActivity().getResources()
                .getString(R.string.intermediate)))
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
                    type = getActivity().getResources().getString(R.string.intermediate)
                            .toLowerCase();
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

        listView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.textview, options));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String first = options.get(position);
                String next = "";
                if (categoryName != null)
                    next = "<font color='#AFA4C4'> - " + type + "</font> (" + categoryName + ")";
                else
                    next = "<font color='#AFA4C4'> - " + type + "</font>";
                subCategoryTextView.setText(Html.fromHtml(first + next));
                subCategoryTextView.setTag(d.get(position).getId());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "Trying to connect with GoogleAPIclient");
        if(mGoogleApiClient != null)
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient != null || mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            mGoogleApiClient.disconnect();
        }

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
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(locationInput.getWindowToken(), 0);
                }
            }
        });

        final Drawable clearInputField = getActivity().getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel);
        clearInputField.setBounds(0, 0, 50, 50);

        /*locationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = locationInput.getText().toString().trim();
                if (s.length() > 0) {
                    locationInput.setCompoundDrawables(null, null, clearInputField, null);
                    getAutoSuggestions(input);
                }
            }
        });
*/

        /*locationInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (locationInput.getRight() - locationInput.getCompoundDrawables()[2].getBounds().width())) {
                            locationInput.setText("");
                            locationInput.setCompoundDrawables(null, null, null, null);
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                }
                return false;
            }
        });*/


        locationInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
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
                if (minute > 52 || minute < 8) {
                    min = "00";
                    if (minute > 52)
                        hour++;
                } else if (minute > 7 && minute < 23)
                    min = "15";
                else if (minute > 22 && minute < 38)
                    min = "30";
                else if (minute > 37 && minute < 53)
                    min = "45";
                fromTimingInput.setText(" " + (hour < 10 ? ("0" + hour) : hour) + ":"
                        + min + (hourOfDay > 11 ? " PM" : " AM"));
                fromTimingInput.setTag(fromTimingInput.getId(), hourOfDay++ + ":" + min);
                getSelectedButtons();
            }
        });

        final ImageView clearFilter = (ImageView) fragmentView.findViewById(R.id.clear);
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

                int hour = c.get(Calendar.HOUR_OF_DAY);
                int min = getSettingMinute(c.get(Calendar.MINUTE));
                if (min == 0)
                    hour++;

                TimePickerFragment timePicker = new TimePickerFragment(getActivity(),
                        HomeFragment.this, hour, min,
                        false);
                timePicker.show();
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        locationInput.setOnItemClickListener(mAutocompleteClickListener);

        locationInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    private void setBounds(Location location, int mDistanceInMeters ){
        Log.e(TAG,"inside setBounds method");
        double latRadian = Math.toRadians(location.getLatitude());

        double degLatKm = 110.574235;
        double degLongKm = 110.572833 * Math.cos(latRadian);
        double deltaLat = mDistanceInMeters / 1000.0 / degLatKm;
        double deltaLong = mDistanceInMeters / 1000.0 / degLongKm;

        double minLat = location.getLatitude() - deltaLat;
        double minLong = location.getLongitude() - deltaLong;
        double maxLat = location.getLatitude() + deltaLat;
        double maxLong = location.getLongitude() + deltaLong;

        Log.d(TAG,"Min: "+Double.toString(minLat)+","+Double.toString(minLong));
        Log.d(TAG,"Max: "+Double.toString(maxLat)+","+Double.toString(maxLong));

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), R.layout.textview,
                mGoogleApiClient, new LatLngBounds(new LatLng(minLat, minLong), new LatLng(maxLat, maxLong)), null);
        locationInput.setAdapter(mAdapter);
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            /*Toast.makeText(getApplicationContext(), "Clicked: " + item.description,
                    Toast.LENGTH_SHORT).show();*/
            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    private int getSettingMinute(int i) {
        if (i < 16)
            return 1;
        else if (i < 31)
            return 2;
        else if (i < 46)
            return 3;
        return 0;
    }

    private void getSelectedButtons() {
        daysButton = new ArrayList<>();

        daysButton.add((ChizzleButton) fragmentView.findViewById(R.id.sun));
        daysButton.add((ChizzleButton) fragmentView.findViewById(R.id.mon));
        daysButton.add((ChizzleButton) fragmentView.findViewById(R.id.tue));
        daysButton.add((ChizzleButton) fragmentView.findViewById(R.id.wed));
        daysButton.add((ChizzleButton) fragmentView.findViewById(R.id.thu));
        daysButton.add((ChizzleButton) fragmentView.findViewById(R.id.fri));
        daysButton.add((ChizzleButton) fragmentView.findViewById(R.id.sat));

        for (final Button b : daysButton) {
            b.setTag(b.getId(), 0);

            b.setBackgroundDrawable(getActivity().getResources()
                    .getDrawable(R.drawable.custom_rounded_corner_purple_light));
            b.setTextColor(getActivity().getResources().getColor(R.color.white));

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (Integer) b.getTag(b.getId());
                    if (tag == 0) {
                        b.setTag(b.getId(), 1);
                        b.setBackgroundDrawable(getActivity().getResources()
                                .getDrawable(R.drawable.custom_rounded_corner_white));
                        b.setTextColor(getActivity().getResources().getColor(R.color.purple));
                    } else {
                        b.setTag(b.getId(), 0);
                        b.setBackgroundDrawable(getActivity().getResources()
                                .getDrawable(R.drawable.custom_rounded_corner_purple_light));
                        b.setTextColor(getActivity().getResources().getColor(R.color.white));
                    }

                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            profileResponse = new Gson().fromJson(StorageHelper.getUserProfile(getActivity()), ProfileResponse.class);
            Log.e(TAG, StorageHelper.getUserProfile(getActivity()));
            com.findmycoach.app.beans.student.Data userInfo = profileResponse.getData();

            if (userInfo.getTrainingLocation() != null && !userInfo.getTrainingLocation().toString().isEmpty())
                location = userInfo.getTrainingLocation().toString();

            coachingType = profileResponse.getData().getCoachingType();
            mentorFor = Integer.parseInt(profileResponse.getData().getMentorFor());

            if (mentorFor == 0) {
                numberOfTabsToBeShown = 1;
            } else if (mentorFor == 1) {
                try {
                    numberOfTabsToBeShown = profileResponse.getData().getChildren().size();
                } catch (Exception e) {
                    numberOfTabsToBeShown = 1;
                    mentorFor = 0;
                }
            } else if (mentorFor == 2) {
                try {
                    numberOfTabsToBeShown = profileResponse.getData().getChildren().size();
                    numberOfTabsToBeShown++;
                } catch (Exception e) {
                    numberOfTabsToBeShown = 1;
                    mentorFor = 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (location == null)
            location = "";

        updateLocationUI();
    }

    private void initialize(View view) {
        isEditLocationEnabled = false;
        numberOfTabsToBeShown = 1;
        rlAutoSuggestionAddress = (RelativeLayout) view.findViewById(R.id.rlAutoSuggestionAddress);
        ibDeleteAddressImageButton = (ImageButton) view.findViewById(R.id.ibDeleteAddressImageButton);
        ibDeleteAddressImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!locationInput.getText().toString().trim().isEmpty()) {
                    locationInput.setText("");
                }
            }
        });
        locationInput = (AutoCompleteTextView) view.findViewById(R.id.input_location);
        currentLocationText = (TextView) view.findViewById(R.id.current_location_text_view);
        searchButton = (Button) view.findViewById(R.id.action_search);
        editLocation = (RelativeLayout) view.findViewById(R.id.edit_location);
        selectLocation = (RelativeLayout) view.findViewById(R.id.select_location);
        editLocation.setOnClickListener(this);
        selectLocation.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        timeBarrierLayout = (LinearLayout) view.findViewById(R.id.time_barrier_layout);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        timeBarrier = false;
        type = getResources().getString(R.string.beginner);
        subCategoryLayout = (LinearLayout) fragmentView.findViewById(R.id.subCategoryLayout);
        subCategoryTextView = (TextView) fragmentView.findViewById(R.id.subCategoryTextView);

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
            isEditLocationEnabled = true;
            rlAutoSuggestionAddress.setVisibility(View.VISIBLE);
            //locationInput.setVisibility(View.VISIBLE);
            currentLocationText.setVisibility(View.GONE);
            editLocation.setVisibility(View.GONE);
        } else {
            currentLocationText.setText(location);
            locationInput.setText(location);
            isEditLocationEnabled = false;
            rlAutoSuggestionAddress.setVisibility(View.GONE);
            //locationInput.setVisibility(View.GONE);
            editLocation.setVisibility(View.VISIBLE);
            currentLocationText.setVisibility(View.VISIBLE);
        }
    }

    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key", getResources().getString(R.string.google_location_api_key));
        NetworkClient.autoComplete(getActivity(), requestParams, this, 32);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.action_search) {
            callSearchApi();
        } else if (id == R.id.edit_location) {
            isEditLocationEnabled = true;
            rlAutoSuggestionAddress.setVisibility(View.VISIBLE);
            //locationInput.setVisibility(View.VISIBLE);
            editLocation.setVisibility(View.GONE);
            currentLocationText.setVisibility(View.GONE);
        } else if (id == R.id.select_location) {
            isEditLocationEnabled = false;
            rlAutoSuggestionAddress.setVisibility(View.GONE);
            //locationInput.setVisibility(View.GONE);
            currentLocationText.setVisibility(View.VISIBLE);
            editLocation.setVisibility(View.VISIBLE);
            showLocationPickerDialog();
        }
    }

    private void showLocationPickerDialog() {
        final List<String> addressList = new ArrayList<>();
        addressList.add(getResources().getString(R.string.pick_current_location));
        for (Address address : profileResponse.getData().getMultipleAddress()) {
            addressList.add(address.getLocale());
        }

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_address_for_search);
        ListView listView = (ListView) dialog.findViewById(R.id.localityListView);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.textview, addressList));
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (DashboardActivity.dashboardActivity.userCurrentAddressFromGPS.trim().isEmpty())
                        DashboardActivity.dashboardActivity.getAddress();
                    else {
                        currentLocationText.setText(DashboardActivity.dashboardActivity.userCurrentAddressFromGPS.trim());
                        locationInput.setText(DashboardActivity.dashboardActivity.userCurrentAddressFromGPS.trim());
                    }
                } else {
                    currentLocationText.setText(addressList.get(position));
                    locationInput.setText(addressList.get(position));
                }
                dialog.dismiss();
            }
        });

    }

    private void callSearchApi() {
        if (!NetworkManager.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources()
                    .getString(R.string.check_network_connection), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isSearching) {
            Toast.makeText(getActivity(), getResources()
                    .getString(R.string.search_is_already_called), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEditLocationEnabled) {
            if (validateLocation()) {
                goForSearch();
            }
        } else {
            goForSearch();
        }

    }

    void goForSearch() {
        isSearching = true;
        String location = "";
        if (isEditLocationEnabled)
            location = locationInput.getText().toString();
        else
            location = currentLocationText.getText().toString();
        RequestParams requestParams = new RequestParams();
        requestParams.add("location", location);
        requestParams.add("subcategory_id", subCategoryTextView.getTag() + "");
        requestParams.add("class_type", String.valueOf(coachingType));
        Log.e(TAG, "subcategory_id : " + subCategoryTextView.getTag() + " == " + location);

        if (timeBarrier) {
            String fromTiming = (String) fromTimingInput.getTag(fromTimingInput.getId());


            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                Calendar c = Calendar.getInstance();
                date = format.parse(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
                        + c.get(Calendar.DATE) + " " + fromTiming + ":00");
                Log.e(TAG, "Time Stamp : " + (c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
                        + c.get(Calendar.DATE) + " " + fromTiming + ":00"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long timestamp = date.getTime() / 1000;

            requestParams.add("time_around", String.valueOf(timestamp));
            Log.e(TAG, "Time Stamp : " + String.valueOf(timestamp));

            String week = "";

            String[] daysArray = new String[]{"6", "0", "1", "2", "3", "4", "5"};
            for (int x = 0; x < 7; x++) {
                Button b = daysButton.get(x);
                String val = b.getTag(b.getId()) + "";
                if (val.trim().equals("1")) {
                    week = week + "," + daysArray[x];
                }
            }
            try {
                week = week.replaceFirst(",", "");
                week = "[" + week + "]";
            } catch (Exception ignored) {
            }
            requestParams.add("days_of_week", week);
            Log.d(TAG, "Selected weekdays : " + week);
        }
        requestParams.add("id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        Intent intent = new Intent(getActivity(), UserListActivity.class);
        intent.putExtra("request_params", requestParams.toString());
        intent.putExtra("no_of_tabs", numberOfTabsToBeShown <= 0 ? 1 : numberOfTabsToBeShown);
        intent.putExtra("mentor_for", mentorFor);
        intent.putExtra("search_for", subCategoryTextView.getText().toString());
        if (timeBarrier) {
            intent.putExtra("show_time_navigation", true);
            intent.putExtra("around_time", fromTimingInput.getText().toString().trim());
        }
        isSearching = false;
        startActivity(intent);
    }


    boolean validateLocation() {
        if (locationInput.getText().toString().trim().equalsIgnoreCase("")) {
            locationInput.setError(getResources().getString(R.string.enter_address));
            if (!locationInput.hasFocus())
                locationInput.clearFocus();
            locationInput.requestFocus();
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
            setTabForCategory(new Gson().fromJson((String) object, Category.class));
            DataBase dataBase = DataBase.singleton(getActivity());
            dataBase.insertData(new Gson().toJson(object));
        }
    }

    private void updateAutoSuggestion(Suggestion suggestion) {
        try {
            ArrayList<String> list = new ArrayList<String>();
            List<Prediction> suggestions = suggestion.getPredictions();
            for (int index = 0; index < suggestions.size(); index++) {
                list.add(suggestions.get(index).getDescription());
            }
            arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.textview, list);
            locationInput.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        //   progressDialog.dismiss();
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
        String min = "";
        if (minute == 0)
            min = "00";
        else if (minute == 1)
            min = "15";
        else if (minute == 2)
            min = "30";
        else if (minute == 3)
            min = "45";
        fromTimingInput.setText(" " + (hour < 10 ? ("0" + hour) : hour) + ":" + min
                + (hourOfDay > 11 ? " PM" : " AM"));
        fromTimingInput.setTag(fromTimingInput.getId(), hourOfDay + ":" + min);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG,"on GoogleApIclient connected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            Log.e(TAG,"location null");

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            Log.e(TAG,"location not null");

            setBounds(location,5500);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG,"on GoogleApIclient connection suspend");

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG,"location changed");

        setBounds(location,5500);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }



}
