package com.findmycoach.app.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.Promotions;
import com.findmycoach.app.beans.UserNotifications.ConnectionRequest;
import com.google.android.gms.analytics.ecommerce.Promotion;

import java.util.ArrayList;

/**
 * Created by ved on 10/8/15.
 */
public class PromotionsRecyclerViewAdapter extends RecyclerView.Adapter<PromotionsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Promotions> promotionsArrayList;
    boolean promotionsYN = false;
    Context context;
    boolean active_promotions;


    public PromotionsRecyclerViewAdapter(Context context, ArrayList<Promotions> promotionsArrayList, boolean activePromotions) {
        promotionsYN = false;
        this.promotionsArrayList = promotionsArrayList;
        if (promotionsArrayList != null && promotionsArrayList.size() > 0) {
            promotionsYN = true;
        }
        this.active_promotions = activePromotions;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promotion_description, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (promotionsYN) {
            holder.tv_no_promotions_message.setVisibility(View.GONE);
            holder.rlPromotionDetails.setVisibility(View.VISIBLE);
            holder.iv_delete_promotion.setVisibility(View.VISIBLE);
            if(active_promotions){
                populatePromotions(holder,position,true);
            }else{
                populatePromotions(holder,position,false);

            }


        } else {
       /*
       * when there is no promotions
       * */
            holder.rlPromotionDetails.setVisibility(View.GONE);
            holder.iv_delete_promotion.setVisibility(View.GONE);
            holder.tv_no_promotions_message.setVisibility(View.VISIBLE);
            if (active_promotions) {
                holder.tv_no_promotions_message.setText(context.getResources().getString(R.string.no_active_promotions));
            } else {
                holder.tv_no_promotions_message.setText(context.getResources().getString(R.string.no_inactive_promotions));

            }
        }

    }

    private void populatePromotions(ViewHolder viewHolder,int position,boolean activePromotions) {


    }


    @Override
    public int getItemCount() {
        if (promotionsYN) {
            return promotionsArrayList.size();
        } else {
            return 1;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlPromotionDetails, rlPromotionTitle, rlPromotionType, rlPromotionDiscount, rlPromotionFreeClass, rlPromotionMandatoryClasses;
        TextView tv_no_promotions_message, tv_promotions_title, tv_promotions_type, tv_promotions_discount, tv_promotions_free_classes, tv_promotions_mandatory_classes;
        ImageView iv_delete_promotion;

        public ViewHolder(View itemView) {
            super(itemView);
            rlPromotionDetails = (RelativeLayout) itemView.findViewById(R.id.rlPromotionsDetails);
            rlPromotionTitle = (RelativeLayout) itemView.findViewById(R.id.rlPromotionTitle);
            rlPromotionType = (RelativeLayout) itemView.findViewById(R.id.rlPromotionType);
            rlPromotionDiscount = (RelativeLayout) itemView.findViewById(R.id.rlDiscountPercentage);
            rlPromotionFreeClass = (RelativeLayout) itemView.findViewById(R.id.rlFreeClasses);
            rlPromotionMandatoryClasses = (RelativeLayout) itemView.findViewById(R.id.rlMandatoryClasses);
            tv_no_promotions_message = (TextView) itemView.findViewById(R.id.tv_no_promotion_val);
            tv_promotions_title = (TextView) itemView.findViewById(R.id.tv_promotion_title_val);
            tv_promotions_type = (TextView) itemView.findViewById(R.id.tv_promotion_type_val);
            tv_promotions_discount = (TextView) itemView.findViewById(R.id.tvDiscountPercentageVal);
            tv_promotions_free_classes = (TextView) itemView.findViewById(R.id.tvFreeClassesVal);
            tv_promotions_mandatory_classes = (TextView) itemView.findViewById(R.id.tvMandatoryClassesVal);
            iv_delete_promotion = (ImageView) itemView.findViewById(R.id.iv_cancel);
        }
    }


}
