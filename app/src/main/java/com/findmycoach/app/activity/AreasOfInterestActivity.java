package com.findmycoach.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.InterestsAdapter;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.Datum;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class AreasOfInterestActivity extends Activity implements Callback {

    private static InterestsAdapter adapter;
    private Button saveAction;
    private final String TAG = "FMC";
    private DataBase dataBase;
    private Category category;
    private ListView listView;
    private List<InterestsAdapter.SubCategoryItems> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_areas_of_interest);
        initialize();
        applyActions();
        checkSUbCategory();
    }

    /**
     * Checking whether subcategory is already cached or not
     */
    private void checkSUbCategory() {
        dataBase = DataBase.singleton(this);

        try {
            String categoryData = dataBase.getAll();
            Log.e("AreaOfInterest", categoryData);
            category = new Gson().fromJson(categoryData, Category.class);
        } catch (Exception e) {
            category = null;
            e.printStackTrace();
        }

        /** If not then call api to get sub categories */
        if (category == null || category.getData().size() < 1) {
            Log.d(TAG, "sub category api called");
            getSubCategories();
        } else
            populateData();

    }

    private void populateData() {
        String interestsString = getIntent().getStringExtra("interests");

        if (interestsString != null) {
            String[] interests = interestsString.split(",");

            for (String s : interests) {
                for (InterestsAdapter.SubCategoryItems i : list) {
                    if (i.getItemName().trim().equalsIgnoreCase(s.trim())) {
                        i.setIsSelected(1);
                    }
                }
            }
        }

//        Collections.sort(list, new Comparator<InterestsAdapter.SubCategoryItems>() {
//            @Override
//            public int compare(InterestsAdapter.SubCategoryItems lhs, InterestsAdapter.SubCategoryItems rhs) {
//                return lhs.getItemName().compareToIgnoreCase(rhs.getItemName());
//            }
//        });
//
//        Collections.sort(list, new Comparator<InterestsAdapter.SubCategoryItems>() {
//            @Override
//            public int compare(InterestsAdapter.SubCategoryItems lhs, InterestsAdapter.SubCategoryItems rhs) {
//                return rhs.getIsSelected() - lhs.getIsSelected();
//            }
//        });

        Log.d(TAG, "+========================================================================================");
        Log.d(TAG, "Message : " + category.getMessage());
        Log.d(TAG, "Data Size : " + category.getData().size());

        for (Datum datum : category.getData()) {
            Log.e(TAG, "Name : " + datum.getName() + ", Sub category size : " + datum.getSubCategories().size() + ", Category size : " + datum.getCategories().size()
                    + ", Parent id : " + datum.getParentId() + ", ID : " + datum.getId());

            list.add(new InterestsAdapter.SubCategoryItems(datum.getName(), 0, datum.getCategories().size() <= 0));
        }

        adapter = new InterestsAdapter(this, list);
        listView.setAdapter(adapter);
    }

    /**
     * Calling category api
     */
    private void getSubCategories() {
        RequestParams requestParams = new RequestParams();
        String authToken = StorageHelper.getUserDetails(this, getResources().getString(R.string.auth_token));
        NetworkClient.getCategories(this, requestParams, authToken, this, 34);
    }

    /**
     * Passing data back to calling class
     */
    private void applyActions() {
        saveAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                List<String> listTemp = new ArrayList<>();
                for (InterestsAdapter.SubCategoryItems l : list) {
                    if (l.getIsSelected() == 1)
                        listTemp.add(l.getItemName());
                }
                intent.putExtra("interests", new Gson().toJson(listTemp));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    /**
     * Getting references of views
     */
    private void initialize() {
        listView = (ListView) findViewById(R.id.areas_of_interest_list);
        saveAction = (Button) findViewById(R.id.save_interests);
        list = new ArrayList<>();
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.prompt_area_of_coaching));
    }


    /**
     * If subcategories are successfully retried from server, store it into database
     */
    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        dataBase.insertData((String) object);
        String categoryData = dataBase.getAll();
        Log.e("AreaOfInterest", categoryData);
        category = new Gson().fromJson(categoryData, Category.class);
        if (category != null)
            populateData();
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApi) {

    }
}
