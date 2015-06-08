package com.findmycoach.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.InterestsAdapter;
import com.findmycoach.app.beans.category.Datum;
import com.findmycoach.app.beans.category.DatumSub;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShekharKG on 8/6/15.
 */
public class AreaOfInterestSub extends Activity {

    private int index;
    private ListView listView;
    private Datum datum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areas_of_interest);
        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.areas_of_interest_list);
        TextView title = (TextView) findViewById(R.id.title);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        index = getIntent().getIntExtra("index", -2);

        if (index != -2 && index != AreasOfInterestActivity.category.getData().size()) {
            datum = AreasOfInterestActivity.category.getData().get(index);
            title.setText(datum.getName());

            List<InterestsAdapter.SubCategoryItems> list = new ArrayList<>();
            if (datum.getSubCategories().size() > 0) {
                for (DatumSub sub : datum.getSubCategories())
                    list.add(new InterestsAdapter.SubCategoryItems(sub.getName(), 2));
                InterestsAdapter adapter = new InterestsAdapter(this, list);
                listView.setAdapter(adapter);
            } else if (datum.getCategories().size() > 0) {
                for (Datum sub : datum.getCategories())
                    list.add(new InterestsAdapter.SubCategoryItems(sub.getName(), 1));
                InterestsAdapter adapter = new InterestsAdapter(this, list);
                listView.setAdapter(adapter);
            }

        }
    }
}
