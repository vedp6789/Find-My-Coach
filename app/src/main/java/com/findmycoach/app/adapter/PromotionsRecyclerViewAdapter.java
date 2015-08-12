package com.findmycoach.app.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findmycoach.app.beans.Promotions.Offer;


import com.findmycoach.app.R;
import com.findmycoach.app.beans.Promotions.Promotions;
import com.findmycoach.app.fragment.ActiveInactivePromotions;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

/**
 * Created by ved on 10/8/15.
 */
public class PromotionsRecyclerViewAdapter extends RecyclerView.Adapter<PromotionsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Offer> promotionsArrayList;
    boolean promotionsYN = false;
    Context context;
    boolean active_promotions;
    ActiveInactivePromotions activeInactivePromotions;


    public PromotionsRecyclerViewAdapter(Context context, ArrayList<Offer> promotionsArrayList, boolean activePromotions, ActiveInactivePromotions activeInactivePromotions) {
        promotionsYN = false;
        this.promotionsArrayList = promotionsArrayList;
        if (promotionsArrayList != null && promotionsArrayList.size() > 0) {
            promotionsYN = true;
        }
        this.active_promotions = activePromotions;
        this.context = context;
        this.activeInactivePromotions = activeInactivePromotions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promotion_description, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    void updateOfferArrayList(ArrayList<Offer> offers) {
        promotionsArrayList = offers;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (promotionsYN) {
            holder.tv_no_promotions_message.setVisibility(View.GONE);
            holder.rlPromotionDetails.setVisibility(View.VISIBLE);
            holder.iv_delete_promotion.setVisibility(View.VISIBLE);
            if (active_promotions) {
                populatePromotions(holder, position, true);
            } else {
                populatePromotions(holder, position, false);
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

    private void populatePromotions(final ViewHolder holder, final int position, final boolean active_promotions) {
        try {

            Offer offer = promotionsArrayList.get(position);
            holder.tv_promotions_title.setText(offer.getPromotion_title());
            final String promotion_type = offer.getPromotion_type();
            final String offer_id = offer.getId();
            if (promotion_type.equals("discount")) {
                holder.rlPromotionFreeClass.setVisibility(View.GONE);
                holder.rlPromotionMandatoryClasses.setVisibility(View.GONE);
                holder.tv_promotions_type.setText(context.getResources().getString(R.string.discount_type_promotion));
                holder.tv_promotions_discount.setText(offer.getDiscount_percentage());

            } else {
                if (promotion_type.equals("trial")) {
                    holder.rlPromotionDiscount.setVisibility(View.GONE);
                    holder.tv_promotions_type.setText(context.getResources().getString(R.string.free_classes_promotion));
                    holder.tv_promotions_free_classes.setText(offer.getFree_classes());   /* This is the number of classes mentor will give as free to mentee*/
                    holder.tv_promotions_mandatory_classes.setText(offer.getFree_min_classes());  /* This is number of mandatory classes which mentee have to join to get free classes */

                }
            }

            holder.iv_delete_promotion.setOnClickListener(null);
            holder.iv_delete_promotion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (active_promotions) {
                        showDialog(offer_id, true);
                    } else {
                        showDialog(offer_id, false);

                    }

                }

                private void showDialog(final String offer_id, boolean b) {
                    holder.tv_title.setText(context.getResources().getString(R.string.promotion));
                    if (b) {
                        holder.tv_message.setText(context.getResources().getString(R.string.either_inactivate_or_delete));
                        holder.b1.setText(context.getResources().getString(R.string.delete));
                        holder.b2.setText(context.getResources().getString(R.string.inactivate));
                        holder.b_back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.dialog.dismiss();
                            }
                        });
                        holder.b1.setOnClickListener(null);
                        holder.b1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestParams requestParams = new RequestParams();
                                requestParams.add("promotion_id", offer_id);
                                if (activeInactivePromotions != null) {
                                    activeInactivePromotions.deletePromotion(requestParams, position, holder.dialog);
                                } else {
                                    Log.e("FMC", "ActiveInactivePromotions instance null");
                                }
                            }
                        });
                        holder.b2.setOnClickListener(null);

                        holder.b2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestParams requestParams = new RequestParams();
                                requestParams.add("promotion_id", offer_id);
                                requestParams.add("promotion_type", promotion_type);
                                requestParams.add("is_active", "0");  /* activate to inactive*/
                                if (activeInactivePromotions != null) {
                                    activeInactivePromotions.updatePromotion(requestParams, position, holder.dialog);
                                } else {
                                    Log.e("FMC", "ActiveInactivePromotions instance null");
                                }
                            }
                        });
                    } else {
                        holder.tv_message.setText(context.getResources().getString(R.string.either_activate_or_delete));
                        holder.b1.setText(context.getResources().getString(R.string.delete));
                        holder.b2.setText(context.getResources().getString(R.string.activate));
                        holder.b_back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.dialog.dismiss();
                            }
                        });

                        holder.b1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestParams requestParams = new RequestParams();
                                requestParams.add("promotion_id", offer_id);
                                if (activeInactivePromotions != null) {
                                    activeInactivePromotions.deletePromotion(requestParams, position, holder.dialog);
                                } else {
                                    Log.e("FMC", "ActiveInactivePromotions instance null");
                                }
                            }
                        });

                        holder.b2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestParams requestParams = new RequestParams();
                                requestParams.add("promotion_id", offer_id);
                                requestParams.add("promotion_type", promotion_type);
                                requestParams.add("is_active", "1");  /* Inactivate to activate*/
                                if (activeInactivePromotions != null) {
                                    activeInactivePromotions.updatePromotion(requestParams, position, holder.dialog);
                                } else {
                                    Log.e("FMC", "ActiveInactivePromotions instance null");
                                }
                            }
                        });
                    }
                    holder.dialog.show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Dialog dialog;
        TextView tv_message, tv_title;
        Button b1, b2;
        ImageView b_back;

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

            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_action_on_promotion);
            //myDialog.setTitle();
            dialog.setCancelable(false);
            tv_message = (TextView) dialog.findViewById(R.id.tv_message);
            tv_title = (TextView) dialog.findViewById(R.id.title);
            b_back = (ImageView) dialog.findViewById(R.id.backButton);
            b1 = (Button) dialog.findViewById(R.id.b1);
            b2 = (Button) dialog.findViewById(R.id.b2);


        }
    }


}
