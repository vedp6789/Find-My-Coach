package com.findmycoach.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    private final String TAG = "AreaOfInterestSub";
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areas_of_interest);

        listView = (ListView) findViewById(R.id.areas_of_interest_list);
        title = (TextView) findViewById(R.id.title);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {

        index = getIntent().getIntExtra("index", -2);
        int level = getIntent().getIntExtra("level", -1);

        findViewById(R.id.buttonSave).setVisibility(View.GONE);

        Datum datum = null;
        if (index != -2 && index != AreasOfInterestActivity.category.getData().size()
                && level == 2) {
            datum = AreasOfInterestActivity.category.getData().get(index);
            title.setText(datum.getName());

            final List<InterestsAdapter.SubCategoryItems> list = new ArrayList<>();
            if (datum.getSubCategories().size() > 0) {
                for (DatumSub sub : datum.getSubCategories())
                    list.add(new InterestsAdapter.SubCategoryItems(sub.getName(),
                            sub.isSelected() ? 3 : 2));
                populateListAddListener(list, datum.getSubCategories());
            } else if (datum.getCategories().size() > 0) {
                for (Datum sub : datum.getCategories()) {
                    String title = sub.getName();
                    if (sub.getSelectedItems() > 0)
                        title = title + "<font color='#AFA4C4'> - " + sub.getSelectedItems()
                                + " " + getResources().getString(R.string.selected) + "</font>";
                    list.add(new InterestsAdapter.SubCategoryItems(title, 1));
                }
                InterestsAdapter adapter = new InterestsAdapter(this, list);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent intent = new Intent(AreaOfInterestSub.this,
                                AreaOfInterestSub.class);
                        intent.putExtra("index", index);
                        intent.putExtra("level", 3);
                        intent.putExtra("sub_level_index", position);
                        startActivity(intent);
                    }
                });
            }
        } else if (level == 3 && index != -2) {
            datum = AreasOfInterestActivity.category.getData().get(index);
            int subIndex = getIntent().getIntExtra("sub_level_index", 0);
            final List<InterestsAdapter.SubCategoryItems> list = new ArrayList<>();
            Datum datum1 = datum.getCategories().get(subIndex);
            title.setText(datum1.getName());

            for (DatumSub sub : datum1.getSubCategories())
                list.add(new InterestsAdapter.SubCategoryItems(sub.getName(),
                        sub.isSelected() ? 3 : 2));
            populateListAddListener(list, datum1.getSubCategories());

        } else if (index != -2 && index == AreasOfInterestActivity.category.getData().size()
                && level == 2) {
            final List<InterestsAdapter.SubCategoryItems> list = new ArrayList<>();
            for (DatumSub datumSub : AreasOfInterestActivity.datumSubs) {
                list.add(new InterestsAdapter.SubCategoryItems(datumSub.getName(),
                        datumSub.isSelected() ? 3 : 2));
            }
            title.setText(getResources().getString(R.string.all));
            populateListAddListener(list, AreasOfInterestActivity.datumSubs);
        }
    }

    /**
     * Items are child
     */
    private void populateListAddListener(final List<InterestsAdapter.SubCategoryItems> list,
                                         final List<DatumSub> datumSubList) {
        final InterestsAdapter adapter = new InterestsAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InterestsAdapter.SubCategoryItems item = list.get(position);
//                Log.d(TAG, datumSubList.get(position).getName() + " : " +
//                        datumSubList.get(position).isSelected());
                if (item.getValue() == 2) {
                    item.setValue(3);
                    datumSubList.get(position).setIsSelected(true);
                    updateParentCategorySelectedItems(datumSubList.get(position), 1);

                } else {
                    item.setValue(2);
                    datumSubList.get(position).setIsSelected(false);
                    updateParentCategorySelectedItems(datumSubList.get(position), -1);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void updateParentCategorySelectedItems(DatumSub datumSub, int i) {
        for (Datum datum : AreasOfInterestActivity.category.getData()) {
            for (DatumSub datumSub1 : datum.getSubCategories()) {
                if (datumSub.getId().equals(datumSub1.getId())) {
                    datum.setSelectedItems(datum.getSelectedItems() + i);
//                    Log.e(TAG, i + " : " + datum.getName() + " : " + datumSub.getName());
                    return;
                }
            }

            for (Datum datum1 : datum.getCategories()) {
                for (DatumSub datumSub1 : datum1.getSubCategories()) {
                    if (datumSub.getId().equals(datumSub1.getId())) {
                        datum1.setSelectedItems(datum1.getSelectedItems() + i);
//                        Log.e(TAG, i + " : " + datum1.getName() + " : " + datumSub1.getName());
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }
}
