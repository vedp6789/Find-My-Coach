package com.findmycoach.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.findmycoach.app.R;

/**
 * Created by prem on 5/3/15.
 */
public class SubCategoryActivity extends Activity{

    private int rowNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);


        Intent intent = getIntent();
        if(intent != null) {
            rowNumber = intent.getIntExtra("row", 0);
        }

        if(rowNumber > 0) {
            updateSpinnerValues(intent.getStringExtra("category"));
        }
    }

    private void updateSpinnerValues(String category) {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerSubCategory);
        String[] rowValue = new String[rowNumber];
        for(int i=0; i<rowNumber; i++)
            rowValue[i] = category + " : " + (i+1);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, rowValue));
    }
}
