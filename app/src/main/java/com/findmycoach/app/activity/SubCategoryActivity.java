package com.findmycoach.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment_mentee.HomeFragment;

/**
 * Created by prem on 5/3/15.
 */
public class SubCategoryActivity extends Activity{

    private int rowNumber, columnNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);


        Intent intent = getIntent();
        if(intent != null) {
            rowNumber = intent.getIntExtra("row", 0);
            columnNumber = intent.getIntExtra("column_index",0);
            if(rowNumber > 0) {
                updateSpinnerValues(intent.getStringExtra("sub_category"), intent.getStringExtra("sub_category_id"));
            }
        }

    }

    private void updateSpinnerValues(String subCat, String subCatId) {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerSubCategory);
        final String[] rowValue = new String[rowNumber];
        final String[] subCategory = subCat.split("#");
        final String[] subCategoryId = subCatId.split("#");
        for(int i=0; i<rowNumber; i++) {
            rowValue[i] = subCategory[i];
        }
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, rowValue));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    HomeFragment.subCategoryIds[columnNumber] = subCategoryId[position];
                }catch (Exception e){
                    e.printStackTrace();
                }

                try{
                    if(position != 0 && !AreasOfInterestActivity.list.contains(rowValue[position])) {
                        AreasOfInterestActivity.list.add(rowValue[position]);
                        AreasOfInterestActivity.notifyAdapter();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
