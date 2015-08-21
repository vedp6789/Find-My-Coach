package com.findmycoach.app.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.MyPromotions;
import com.findmycoach.app.adapter.PromotionsRecyclerViewAdapter;
import com.findmycoach.app.beans.Promotions.Promotions;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DividerItemDecoration;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.RecyclerItemClickListener;
import com.findmycoach.app.beans.Promotions.Offer;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ved on 10/8/15.
 */
public class ActiveInactivePromotions extends Fragment implements Callback {
    ArrayList<Offer> promotionsArrayList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int selectedPosition = -1;
    private boolean activePromotions;
    ActiveInactivePromotions activeInactivePromotions;
    int position_which_got_deleted_or_updated = -1;
    Dialog dialog;


    ProgressDialog progressDialog;

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
        activeInactivePromotions = this;
        progressDialog = new ProgressDialog(getActivity());
        dialog = null;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activeInactivePromotions = null;
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
        adapter = new PromotionsRecyclerViewAdapter(getActivity(), promotionsArrayList, activePromotions, activeInactivePromotions);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);
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

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.scrollToPosition(0);
    }

    public void deletePromotion(RequestParams requestParams, int position, Dialog dialog, JSONArray jsonArray) {
        // this.dialog =dialog;
        //progressDialog.show();
        position_which_got_deleted_or_updated = position;
        //    NetworkClient.deletePromotion(getActivity(), requestParams, StorageHelper.getUserGroup(getActivity(), "auth_token"), this, 60);
        try {
            NetworkClient.deletePromotionHttpClient(getActivity(), StorageHelper.getUserGroup(getActivity(), "auth_token"), jsonArray, this, 60);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*public void updatePromotion(RequestParams requestParams, int position, Dialog dialog) {
        this.dialog = dialog;
        progressDialog.show();
        position_which_got_deleted_or_updated = position;
        NetworkClient.addPromotion(getActivity(), requestParams, StorageHelper.getUserGroup(getActivity(), "auth_token"), this, 59);
    }*/

    public void updatePromotion(RequestParams requestParams, int position) {
        progressDialog.show();
        position_which_got_deleted_or_updated = position;
        NetworkClient.addPromotion(getActivity(), requestParams, StorageHelper.getUserGroup(getActivity(), "auth_token"), this, 59);
    }


    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        try {
           /* if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }*/
            progressDialog.dismiss();
            if (calledApiValue == 60) {
                JSONObject jsonObject = (JSONObject) object;
                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                if (position_which_got_deleted_or_updated >= 0)
                    promotionsArrayList.remove(position_which_got_deleted_or_updated);
                adapter.notifyDataSetChanged();
                position_which_got_deleted_or_updated = -1;
            }
            if (calledApiValue == 59) {
                JSONObject jsonObject = (JSONObject) object;
                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                if (position_which_got_deleted_or_updated >= 0)
                    promotionsArrayList.remove(position_which_got_deleted_or_updated);
                adapter.notifyDataSetChanged();
                position_which_got_deleted_or_updated = -1;
                if (MyPromotions.myPromotions != null) {
                    if (activePromotions) {
                        MyPromotions.myPromotions.getAllPromotions(false);
                    } else {
                        MyPromotions.myPromotions.getAllPromotions(true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Log.e("FMC", "ActiveInactivePromotions " + calledApiValue + " failure");
        position_which_got_deleted_or_updated = -1;
    }

}
