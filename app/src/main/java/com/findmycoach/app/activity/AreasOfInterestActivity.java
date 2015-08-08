package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.List;

public class AreasOfInterestActivity extends Activity implements Callback {

    private final String TAG = "FMC";
    private DataBase dataBase;
    public static Category category;
    public static List<DatumSub> datumSubs;
    private ListView listView;
    private ProgressDialog progressDialog;
    private final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_areas_of_interest);
        initialize();
        checkSUbCategory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        category = null;
        datumSubs = null;
    }

    /**
     * Checking whether subcategory is already cached or not
     */
    private void checkSUbCategory() {
        dataBase = DataBase.singleton(this);

        try {
            String categoryData = dataBase.getAll();
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
        try {
            String interestsString = getIntent().getStringExtra("interests");
            List<String> selectedAreaOfInterest = new ArrayList<>();
            if (interestsString != null) {
                Collections.addAll(selectedAreaOfInterest, interestsString.split(","));
                for (int i = 0; i < selectedAreaOfInterest.size(); i++)
                    selectedAreaOfInterest.set(i, selectedAreaOfInterest.get(i).trim());
            }


            for (Datum datum : category.getData()) {
                for (DatumSub datumSub : datum.getSubCategories()) {
                    datumSubs.add(datumSub);
                    if (selectedAreaOfInterest.contains(datumSub.getName().trim())) {
                        datumSub.setIsSelected(true);
                        datum.setSelectedItems(datum.getSelectedItems() + 1);
                    }
                }

                for (Datum datum1 : datum.getCategories())
                    for (DatumSub datumSub : datum1.getSubCategories()) {
                        datumSubs.add(datumSub);
                        if (selectedAreaOfInterest.contains(datumSub.getName().trim())) {
                            datumSub.setIsSelected(true);
                            datum1.setSelectedItems(datum1.getSelectedItems() + 1);
                        }
                    }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        updateMainList();

    }

    private void updateMainList() {
        if (category == null || category.getData().size() < 1)
            return;

        List<InterestsAdapter.SubCategoryItems> list = new ArrayList<>();
        for (Datum datum : category.getData()) {
            Log.e(TAG, "Name : " + datum.getName() + ", Sub category size : "
                    + datum.getSubCategories().size() + ", Category size : "
                    + datum.getCategories().size()
                    + ", Parent id : " + datum.getParentId() + ", ID : " + datum.getId()
                    + ", Selected items : " + datum.getSelectedItems());

            String title = datum.getName();

            if (datum.getSelectedItems() > 0)
                title = title + "<font color='#AFA4C4'> - " + datum.getSelectedItems()
                        + " " + getResources().getString(R.string.selected) + "</font>";

            if (datum.getCategories().size() > 0) {
                int count = 0;
                for (Datum datum1 : datum.getCategories()) {
                    count = count + datum1.getSelectedItems();
                }

                if (count > 0)
                    title = title + "<font color='#AFA4C4'> - " + count
                            + " " + getResources().getString(R.string.selected) + "</font>";
            }

            list.add(new InterestsAdapter.SubCategoryItems(title, 1));
        }

        list.add(new InterestsAdapter.SubCategoryItems(getResources().getString(R.string.all), 1));

        InterestsAdapter adapter = new InterestsAdapter(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AreasOfInterestActivity.this, AreaOfInterestSub.class);
                intent.putExtra("index", position);
                intent.putExtra("level", 2);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMainList();
    }

    /**
     * Calling category api
     */
    private void getSubCategories() {
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        String authToken = StorageHelper.getUserDetails(this, getResources().getString(R.string.auth_token));
        NetworkClient.getCategories(this, requestParams, authToken, this, 34);
    }

    /**
     * Getting references of views
     */
    private void initialize() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        category = null;
        datumSubs = new ArrayList<>();
        listView = (ListView) findViewById(R.id.areas_of_interest_list);
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.prompt_area_of_coaching));

        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnResult();
            }
        });
    }

    private void returnResult() {
        Intent intent = new Intent();
        List<String> listTemp = new ArrayList<>();
        for (DatumSub d : datumSubs) {
            if (d.isSelected())
                listTemp.add(d.getName());
        }
        intent.putExtra("interests", new Gson().toJson(listTemp));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            returnResult();
        }
    }

    /**
     * If subcategories are successfully retried from server, store it into database
     */
    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        dataBase.insertData(new Gson().toJson(object));
        String categoryData = dataBase.getAll();
        Log.e(TAG, categoryData);
        category = new Gson().fromJson(categoryData, Category.class);
        if (category != null) {
            updateMainList();
            populateData();
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApi) {
        progressDialog.dismiss();
    }
}
