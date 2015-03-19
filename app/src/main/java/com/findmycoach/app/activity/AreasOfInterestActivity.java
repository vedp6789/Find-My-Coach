package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.InterestsAdapter;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class AreasOfInterestActivity extends Activity implements Callback {

    public static List<String> list;
    private static InterestsAdapter adapter;
    private Button saveAction;
    private final String TAG = "FMC";
    private DataBase dataBase;
    private Category category;
    private Bundle bundle;


    public static void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = savedInstanceState;

        list = null;
        list = new ArrayList<String>();

        setContentView(R.layout.activity_areas_of_interest);
        initialize();
        applyActionbarProperties();
        applyActions();

        dataBase = DataBase.singleton(this);
        category = dataBase.selectAllSubCategory();
        if(category.getData().size() < 1) {
            Log.d(TAG, "sub category api called");
            getSubCategories();
        }
    }

    private void getSubCategories() {
        RequestParams requestParams = new RequestParams();
        String authToken = StorageHelper.getUserDetails(this, getResources().getString(R.string.auth_token));
        NetworkClient.getCategories(this, requestParams, authToken, this, 34);
    }

    private void applyActions() {
        saveAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("interests", new Gson().toJson(list));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initialize() {
        ListView listView = (ListView) findViewById(R.id.areas_of_interest_list);
        saveAction = (Button) findViewById(R.id.save_interests);
        String interestsString = getIntent().getStringExtra("interests");
        if(interestsString != null){
            String[] interests = interestsString.split(",");
            for(String s : interests)
                list.add(s.trim());
        }
        adapter = new InterestsAdapter(this, list);
        listView.setAdapter(adapter);
    }

    private void applyActionbarProperties() {
        ActionBar actionbar = getActionBar();
        if(actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void addInterest(Category category) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_area_of_interest);
        dialog.setTitle(getResources().getString(R.string.select_sub_category));
        TabHost tabHost = (TabHost) dialog.findViewById(R.id.tabhost);
        LocalActivityManager localActivityManager;
        localActivityManager = new LocalActivityManager(this, false);
        localActivityManager.dispatchCreate(bundle);
        tabHost.setup(localActivityManager);

        for(int i=0; i<category.getData().size(); i++){
            com.findmycoach.app.beans.category.Datum datum = category.getData().get(i);
            TabHost.TabSpec singleCategory = tabHost.newTabSpec(i+"");
            singleCategory.setIndicator(datum.getName());
            Intent intent = new Intent(this, SubCategoryActivity.class);
            StringBuilder subCategory = new StringBuilder();
            StringBuilder subCategoryId = new StringBuilder();
            int row = datum.getDataSub().size()+1;
            subCategory.append("Select one#");
            subCategoryId.append("-1#");
            for(int x=0; x<row-1; x++) {
                subCategory.append(datum.getDataSub().get(x).getName() + "#");
                subCategoryId.append(datum.getDataSub().get(x).getId() + "#");
            }
            intent.putExtra("row", row);
            intent.putExtra("sub_category", subCategory.toString());
            intent.putExtra("sub_category_id",subCategoryId.toString());
            intent.putExtra("column_index", i);
            singleCategory.setContent(intent);
            tabHost.addTab(singleCategory);
        }

        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_areas_of_interest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            addInterest(category);
            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        dataBase.insertData((Category) object);
        category = dataBase.selectAllSubCategory();
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApi) {

    }
}
