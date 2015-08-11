package com.findmycoach.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.PromotionsRecyclerViewAdapter;
import com.findmycoach.app.beans.Promotions.Promotions;
import com.findmycoach.app.util.DividerItemDecoration;
import com.findmycoach.app.util.RecyclerItemClickListener;
import com.findmycoach.app.beans.Promotions.Offer;


import java.util.ArrayList;

/**
 * Created by ved on 10/8/15.
 */
public class ActiveInactivePromotions extends Fragment {
    ArrayList<Offer> promotionsArrayList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int selectedPosition = -1;
    private boolean activePromotions;

    public ActiveInactivePromotions() {

    }

    public static ActiveInactivePromotions newInstance(ArrayList<Offer> promotionsArrayList, boolean forActivePromotions) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("promotions", promotionsArrayList);
        bundle.putBoolean("activePromotions", forActivePromotions);
        ActiveInactivePromotions activeInactivePromotions = new ActiveInactivePromotions();
        activeInactivePromotions.setArguments(bundle);
        return activeInactivePromotions;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            promotionsArrayList = getArguments().getParcelableArrayList("promotions");
            activePromotions = getArguments().getBoolean("activePromotions");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promotions, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_promotions);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new PromotionsRecyclerViewAdapter(getActivity(), promotionsArrayList, activePromotions);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (promotionsArrayList != null && promotionsArrayList.size() > 0) {

                        }
                    }
                })
        );
        return view;
    }
}
