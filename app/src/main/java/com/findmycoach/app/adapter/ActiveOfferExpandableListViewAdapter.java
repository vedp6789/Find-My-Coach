package com.findmycoach.app.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.Promotions.Offer;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ved on 2/9/15.
 */
public class ActiveOfferExpandableListViewAdapter implements ExpandableListAdapter {
    ArrayList<Offer> activeOffers;
    Context context;


    public ActiveOfferExpandableListViewAdapter(Context context, ArrayList<Offer> activeOffers) {
        this.activeOffers = activeOffers;
        this.context = context;
    }


    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return activeOffers.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        //return context.getResources().getString(R.string.my_promotions);
        return "Active Promotions";
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return activeOffers.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater layoutInflater= (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.expandable_list_group_view,null);
        }

        TextView textView= (TextView) convertView.findViewById(R.id.tv_expandable_group);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(headerTitle);

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.promotion_description, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.rlPromotionDetails = (RelativeLayout) convertView.findViewById(R.id.rlPromotionsDetails);
            viewHolder.rlPromotionTitle = (RelativeLayout) convertView.findViewById(R.id.rlPromotionTitle);
            viewHolder.rlPromotionType = (RelativeLayout) convertView.findViewById(R.id.rlPromotionType);
            viewHolder.rlPromotionDiscount = (RelativeLayout) convertView.findViewById(R.id.rlDiscountPercentage);
            viewHolder.rlPromotionFreeClass = (RelativeLayout) convertView.findViewById(R.id.rlFreeClasses);
            viewHolder.rlPromotionMandatoryClasses = (RelativeLayout) convertView.findViewById(R.id.rlMandatoryClasses);
            viewHolder.rlDiscountOnClasses = (RelativeLayout) convertView.findViewById(R.id.rlDiscountOnClasses);
            viewHolder.tvDiscountClasses = (TextView) convertView.findViewById(R.id.tvDiscountClasses);
            viewHolder.tvDiscountClassesVal = (TextView) convertView.findViewById(R.id.tvDiscountClassesVal);
            viewHolder.tv_no_promotions_message = (TextView) convertView.findViewById(R.id.tv_no_promotion_val);
            viewHolder.tv_promotions_title = (TextView) convertView.findViewById(R.id.tv_promotion_title_val);
            viewHolder.tv_promotions_type = (TextView) convertView.findViewById(R.id.tv_promotion_type_val);
            viewHolder.tv_promotions_discount = (TextView) convertView.findViewById(R.id.tvDiscountPercentageVal);
            viewHolder.tv_promotions_free_classes = (TextView) convertView.findViewById(R.id.tvFreeClassesVal);
            viewHolder.tv_promotions_mandatory_classes = (TextView) convertView.findViewById(R.id.tvMandatoryClassesVal);
            viewHolder.iv_delete_promotion = (Button) convertView.findViewById(R.id.iv_cancel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (activeOffers != null && activeOffers.size() > 0) {


        }

        if (activeOffers != null && activeOffers.size() > 0) {
            viewHolder.tv_no_promotions_message.setVisibility(View.GONE);
            viewHolder.rlPromotionDetails.setVisibility(View.VISIBLE);
            populatePromotions(viewHolder, childPosition, true);
        } else {
       /*
       * when there is no promotions
       * */
            viewHolder.rlPromotionDetails.setVisibility(View.GONE);
            viewHolder.tv_no_promotions_message.setVisibility(View.VISIBLE);

            viewHolder.tv_no_promotions_message.setText(context.getResources().getString(R.string.no_active_promotions));

        }


        return convertView;

    }

    private void populatePromotions(final ViewHolder holder, final int position, final boolean active_promotions) {
        try {

            Offer offer = activeOffers.get(position);
            holder.tv_promotions_title.setText(offer.getPromotion_title());
            final String promotion_type = offer.getPromotion_type();
            final String offer_id = offer.getId();
            if (promotion_type.equals("discount")) {
                holder.rlPromotionFreeClass.setVisibility(View.GONE);
                holder.rlPromotionMandatoryClasses.setVisibility(View.GONE);
                holder.rlDiscountOnClasses.setVisibility(View.VISIBLE);
                holder.rlPromotionDiscount.setVisibility(View.VISIBLE);
                holder.tv_promotions_type.setText(context.getResources().getString(R.string.discount_type_promotion));
                holder.tv_promotions_discount.setText(offer.getDiscount_percentage() + context.getResources().getString(R.string.percentage));
                holder.tvDiscountClassesVal.setText(offer.getDiscount_over_classes());

            } else {
                if (promotion_type.equals("trial")) {
                    holder.rlPromotionDiscount.setVisibility(View.GONE);
                    holder.rlPromotionFreeClass.setVisibility(View.VISIBLE);
                    holder.rlPromotionMandatoryClasses.setVisibility(View.VISIBLE);
                    holder.tv_promotions_type.setText(context.getResources().getString(R.string.free_classes_promotion));
                    holder.tv_promotions_free_classes.setText(offer.getFree_classes());   /* This is the number of classes mentor will give as free to mentee*/
                    holder.tv_promotions_mandatory_classes.setText(offer.getFree_min_classes());  /* This is number of mandatory classes which mentee have to join to get free classes */
                    holder.rlDiscountOnClasses.setVisibility(View.GONE);
                }
            }


            holder.iv_delete_promotion.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    private class ViewHolder {
        RelativeLayout rlPromotionDetails, rlPromotionTitle, rlPromotionType, rlPromotionDiscount,
                rlPromotionFreeClass, rlPromotionMandatoryClasses, rlDiscountOnClasses;
        TextView tvDiscountClasses, tvDiscountClassesVal, tv_no_promotions_message,
                tv_promotions_title,
                tv_promotions_type, tv_promotions_discount, tv_promotions_free_classes,
                tv_promotions_mandatory_classes;
        Button iv_delete_promotion;

    }


}
