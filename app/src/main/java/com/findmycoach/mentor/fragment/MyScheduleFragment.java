package com.findmycoach.mentor.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.fmc.mentor.findmycoach.R;

public class MyScheduleFragment extends Fragment {
    private GridView timeGrid;
    private GridView eventGrid;

    public MyScheduleFragment() {
        // Required empty public constructor
    }

    public static MyScheduleFragment newInstance(String param1, String param2) {
        MyScheduleFragment fragment = new MyScheduleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_schedule, container, false);
        initialize(view);
        applyListeners();
        return view;
    }

    private void applyListeners() {
        timeGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initialize(View view) {
        timeGrid = (GridView) view.findViewById(R.id.list_time);
        eventGrid = (GridView) view.findViewById(R.id.event_grid);
        timeGrid.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.calender_time, getResources().getStringArray(R.array.time)));
        eventGrid.setAdapter(new EventGridAdapter());
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private class EventGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 7 * 24;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.calender_event, null);
            }
            TextView event = (TextView) view.findViewById(R.id.event_message);
            if (position == 100) {
                event.setText("Physic Class");
                view.setBackgroundColor(Color.WHITE);
            }
            if (position == 28) {
                event.setText("Swimming Class");
                view.setBackgroundColor(Color.YELLOW);
            }
            if (position == 75) {
                event.setText("Iglulabs");
                view.setBackgroundColor(Color.CYAN);
            }
            if (position == 76) {
                view.setBackgroundColor(Color.CYAN);
            }
            if (position == 120) {
                event.setText("Java");
                view.setBackgroundColor(Color.GREEN);
            }
            return view;
        }
    }
}
