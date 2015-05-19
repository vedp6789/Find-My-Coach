package com.findmycoach.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.InterestsAdapter;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.Datum;
import com.findmycoach.app.beans.category.DatumSub;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        category = dataBase.selectAllSubCategory();

        /** If not then call api to get sub categories */
        if (category.getData().size() < 1) {
            Log.d(TAG, "sub category api called");
            getSubCategories();
        } else
            populateData();

    }

    private void populateData() {
        String interestsString = getIntent().getStringExtra("interests");
        Log.e(TAG, interestsString);

        for (Datum d : category.getData()) {
            for (DatumSub sub : d.getDataSub())
                list.add(new InterestsAdapter.SubCategoryItems(sub.getName(), 0));
        }


        if (interestsString != null) {
            String[] interests = interestsString.split(",");

            for (String s : interests) {
                for (InterestsAdapter.SubCategoryItems i : list) {
                    if (i.getItemName().trim().equalsIgnoreCase(s.trim())) {
                        i.setSelected(1);
                    }
                }
            }
        }

        Collections.sort(list, new Comparator<InterestsAdapter.SubCategoryItems>() {
            @Override
            public int compare(InterestsAdapter.SubCategoryItems lhs, InterestsAdapter.SubCategoryItems rhs) {
                return lhs.getItemName().compareToIgnoreCase(rhs.getItemName());
            }
        });

        Collections.sort(list, new Comparator<InterestsAdapter.SubCategoryItems>() {
            @Override
            public int compare(InterestsAdapter.SubCategoryItems lhs, InterestsAdapter.SubCategoryItems rhs) {
                return rhs.isSelected() - lhs.isSelected();
            }
        });


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
                for(InterestsAdapter.SubCategoryItems l : list){
                    if(l.isSelected() == 1)
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
    }


    /**
     * If subcategories are successfully retried from server, store it into database
     */
    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        dataBase.insertData((Category) object);
        category = dataBase.selectAllSubCategory();
        populateData();
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApi) {

    }
}
