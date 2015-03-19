package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.DatumSub;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AreasOfInterestActivity extends Activity implements Callback {

    private ListView listView;
    public static List<String> list = new ArrayList<String>();
    private static InterestsAdapter adapter;
    private Button saveAction;
    private ArrayAdapter<String> autoSuggestionAdapter;
    private final String TAG = "FMC";
    private LocalActivityManager localActivityManager;
    private List<String> categories;
    private Category category;


    public static void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        autoSuggestionAdapter = null;

        setContentView(R.layout.activity_areas_of_interest);
        initialize();
        applyActionbarProperties();
        applyActions();

        localActivityManager = new LocalActivityManager(this, false);
        localActivityManager.dispatchCreate(savedInstanceState);

        DataBase dataBase = DataBase.singleton(this);
        Category categoryFromDb = dataBase.selectAllSubCategory();
        if(categoryFromDb.getData().size() < 1) {
            Log.d(TAG, "sub category api called");
            getSubCategories();
        }
        else {
            Log.d(TAG, "sub category api not called");
            applySubCategoryAdapter(categoryFromDb);
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
        listView = (ListView) findViewById(R.id.areas_of_interest_list);
        saveAction = (Button) findViewById(R.id.save_interests);
        String interestsString = getIntent().getStringExtra("interests");
        if(interestsString != null){
            String[] interests = interestsString.split(",");
            list.addAll(Arrays.asList(interests));
        }
        adapter = new InterestsAdapter(this, list);
        listView.setAdapter(adapter);
    }

    private void applySubCategoryAdapter(Category category) {
        this.category = category;
        final List<DatumSub> data = new ArrayList<DatumSub>();
        categories = new ArrayList<String>();
        for(com.findmycoach.app.beans.category.Datum d : category.getData()){
            for(DatumSub ds : d.getDataSub()){
                data.add(ds);
                categories.add(ds.getName());
            }
        }
        for(String s : categories)
            Log.e(TAG, s);
        autoSuggestionAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, categories);
    }

    private void applyActionbarProperties() {
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    public void editItem(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.add_interest));
        alertDialog.setMessage(getResources().getString(R.string.enter_interest));
        final AutoCompleteTextView input = new AutoCompleteTextView(this);
        if (autoSuggestionAdapter != null) {
            input.setAdapter(autoSuggestionAdapter);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(4, 4, 4, 4);
        input.setLayoutParams(lp);
        input.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittext));
        input.setText(list.get(position));
        alertDialog.setView(input);
        alertDialog.setPositiveButton(getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String inputProductName = input.getText().toString();
                        if (!inputProductName.equals("")) {
                            if (list.contains(inputProductName)) {
                                input.setError(getResources().getString(R.string.data_already_exist));
                            } else {
                                list.remove(position);
                                list.add(position, inputProductName);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }
                    }
                });

        alertDialog.setNegativeButton(getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void addInterest() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_area_of_interest);
        dialog.setTitle(getResources().getString(R.string.select_sub_category));
        TabHost tabHost = (TabHost) dialog.findViewById(R.id.tabhost);
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
            addInterest();
            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteItemFromList(int position) {
        list.remove(list.get(position));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        applySubCategoryAdapter((Category) object);
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApi) {

    }

    private class InterestsAdapter extends BaseAdapter {
        private Context context;

        public InterestsAdapter(Context context, List<String> list) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.area_of_interest_list_item, null);
            }
            TextView itemName = (TextView) view.findViewById(R.id.item_name);
            ImageView itemEdit = (ImageView) view.findViewById(R.id.item_edit);
            ImageView itemDelete = (ImageView) view.findViewById(R.id.item_delete);
            itemEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editItem(position);
                }
            });
            itemDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItemFromList(position);
                }
            });
            itemName.setText(list.get(position));
            return view;
        }
    }
}
