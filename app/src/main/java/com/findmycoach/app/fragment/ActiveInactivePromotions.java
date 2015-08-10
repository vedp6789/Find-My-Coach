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
import com.findmycoach.app.beans.Promotions;
import com.findmycoach.app.util.DividerItemDecoration;
import com.findmycoach.app.util.RecyclerItemClickListener;

import java.util.ArrayList;

/**
 * Created by ved on 10/8/15.
 */
public class ActiveInactivePromotions extends Fragment {
    ArrayList<Promotions> promotionsArrayList;
    private ActiveInactivePromotions activeInactivePromotions;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int selectedPosition = -1;

    public ActiveInactivePromotions() {

    }

    public ActiveInactivePromotions newInstance(ArrayList<Promotions> promotions) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("activePromotions", promotions);
        ActiveInactivePromotions activeInactivePromotions = new ActiveInactivePromotions();
        activeInactivePromotions.setArguments(bundle);

        return activeInactivePromotions;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeInactivePromotions = this;
        promotionsArrayList = new ArrayList<>();
        if (getArguments() != null) {
            promotionsArrayList = getArguments().getParcelableArrayList("activePromotions");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activeInactivePromotions = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_promotions, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_promotions);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new PromotionsRecyclerViewAdapter(getActivity(), promotionsArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (promotionsArrayList != null && promotionsArrayList.size() > 0) {

                            /*Intent intent = new Intent(getActivity(), MentorNotificationActions.class);


                            Bundle bundle = new Bundle();
                            ConnectionRequest connectionRequest = arrayList_of_connection_request.get(position);
                            selectedPosition = position;
                            bundle.putParcelable("conn_req_data", connectionRequest);

                            intent.putExtra("for", "connection_request");
                            intent.putExtra("conn_req_bundle", bundle);
                            if (connectionRequest.getStatus().equals("unread"))
                                startActivityForResult(intent, 12345);*/
                        }
                    }
                })
        );


        return view;



    }
}
