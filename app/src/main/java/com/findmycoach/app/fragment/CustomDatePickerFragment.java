package com.findmycoach.app.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.MentorDetailsActivity;
import com.findmycoach.app.util.StorageHelper;

/**
 * Created by ved on 2/4/15.
 */
public class CustomDatePickerFragment extends android.support.v4.app.DialogFragment implements View.OnClickListener {
    ImageButton ib_prev_month,ib_prev_year,ib_next_month,ib_next_year;
    TextView tv_month,tv_selected_year;
    Button b_ok,b_cancel;
    int init_month, init_year,temp;
    String [] month;
    private String TAG="FMC";
    private String for_which_activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle=this.getArguments();
        for_which_activity=bundle.getString("for");

        if(for_which_activity.equals("MentorDetailsActivity")){
            init_month= MentorDetailsActivity.month;
            temp=init_month;
            init_year=MentorDetailsActivity.year;
        }

        if(for_which_activity.equals("MyScheduleFragment")){
            init_month=MyScheduleFragment.month;
            temp=init_month;
            init_year=MyScheduleFragment.year;
        }



        month=getResources().getStringArray(R.array.months);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.dialog_custom_date_picker, container, false);
        ib_prev_month= (ImageButton) v.findViewById(R.id.ib_prev_month);
        ib_prev_year= (ImageButton) v.findViewById(R.id.ib_prev_year);
        ib_next_month= (ImageButton) v.findViewById(R.id.ib_next_month);
        ib_next_year= (ImageButton) v.findViewById(R.id.ib_next_year);
        ib_prev_month.setOnClickListener(this);
        ib_prev_year.setOnClickListener(this);
        ib_next_month.setOnClickListener(this);
        ib_next_year.setOnClickListener(this);


        tv_month= (TextView) v.findViewById(R.id.tv_month);
        tv_selected_year= (TextView) v.findViewById(R.id.tv_selected_year);

        tv_month.setText(month[init_month-1]);
        tv_selected_year.setText(String.valueOf(init_year));

        b_ok = (Button) v.findViewById(R.id.b_ok);
        b_cancel = (Button) v.findViewById(R.id.b_cancel);
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 3) {

                    if(for_which_activity.equals("MyScheduleFragment")){
                        MyScheduleFragment.month_from_dialog=temp;
                        MyScheduleFragment.year_from_dialog=Integer.parseInt(tv_selected_year.getText().toString());

                        dismiss();
                        MyScheduleFragment.myScheduleFragment.getCalendarDetailsAPICall();
                    }



                }else{
                    if(for_which_activity.equals("MyScheduleFragment")){
                        MyScheduleFragment.month_from_dialog=temp;
                        MyScheduleFragment.year_from_dialog=Integer.parseInt(tv_selected_year.getText().toString());

                        dismiss();
                        MyScheduleFragment.myScheduleFragment.getCalendarDetailsForMentee();
                    }

                    if(for_which_activity.equals("MentorDetailsActivity")){

                        MentorDetailsActivity.month_from_dialog=temp;
                        MentorDetailsActivity.year_from_dialog=Integer.parseInt(tv_selected_year.getText().toString());

                        dismiss();
                        MentorDetailsActivity.mentorDetailsActivity.getCalendarDetailsAPICall();
                    }


                }

            }
        });
        b_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Log.d(TAG,"Initial Month : "+init_month+", Month : "+month[init_month-1]+ " , Initial year : "+init_year);

        Dialog dialog=getDialog();
        dialog.setTitle(getString(R.string.select));
        dialog.setCanceledOnTouchOutside(false);


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_prev_month:
                if(temp == 1){
                    temp=12;
                    --init_year;
                    tv_month.setText(month[temp-1]);
                    tv_selected_year.setText(String.valueOf(init_year));
                }else{
                    --temp;
                    tv_month.setText(month[temp-1]);

                }
                break;
            case R.id.ib_prev_year:
                --init_year;
                tv_selected_year.setText(String.valueOf(init_year));
                break;
            case R.id.ib_next_month:
                if(temp == 12){
                    temp=1;
                    ++init_year;
                    tv_month.setText(month[temp-1]);
                    tv_selected_year.setText(String.valueOf(init_year));
                }else{
                    ++temp;
                    tv_month.setText(month[temp-1]);
                }
                break;
            case R.id.ib_next_year:
                ++init_year;
                tv_selected_year.setText(String.valueOf(init_year));
                break;



        }
    }
}
